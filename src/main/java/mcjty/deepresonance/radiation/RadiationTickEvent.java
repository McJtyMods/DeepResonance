package mcjty.deepresonance.radiation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.items.ItemRadiationSuit;
import mcjty.deepresonance.varia.QuadTree;
import mcjty.varia.GlobalCoordinate;
import mcjty.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.*;

public class RadiationTickEvent {
    public static final int MAXTICKS = 10;
    private int counter = MAXTICKS;
    private static Random random = new Random();

    private static final int EFFECTS_MAX = 18;
    private int counterEffects = EFFECTS_MAX;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent evt) {
        if (evt.phase == TickEvent.Phase.START) {
            return;
        }
        counter--;
        if (counter <= 0) {
            counter = MAXTICKS;

            counterEffects--;
            boolean doEffects = false;
            if (counterEffects <= 0) {
                counterEffects = EFFECTS_MAX;
                doEffects = true;
            }
            serverTick(doEffects);
        }
    }

    private void serverTick(boolean doEffects) {
        World entityWorld = MinecraftServer.getServer().getEntityWorld();
        DRRadiationManager radiationManager = DRRadiationManager.getManager(entityWorld);

        Set<GlobalCoordinate> toRemove = new HashSet<GlobalCoordinate>();
        boolean dirty = false;

        for (Map.Entry<GlobalCoordinate, DRRadiationManager.RadiationSource> source : radiationManager.getRadiationSources().entrySet()) {
            GlobalCoordinate coordinate = source.getKey();
            World world = DimensionManager.getWorld(coordinate.getDimension());
            if (world != null) {
                if (world.getChunkProvider().chunkExists(coordinate.getCoordinate().getX() >> 4, coordinate.getCoordinate().getZ() >> 4)) {
                    // The world is loaded and the chunk containing the radiation source is also loaded.
                    DRRadiationManager.RadiationSource radiationSource = source.getValue();
                    float strength = radiationSource.getStrength();

                    strength -= RadiationConfiguration.strengthDecreasePerTick * MAXTICKS;
                    dirty = true;
                    if (strength <= 0) {
                        toRemove.add(coordinate);
                    } else {
                        radiationSource.setStrength(strength);

                        if (doEffects) {
                            handleRadiationEffects(world, coordinate, radiationSource);
                        }

                        if (strength > RadiationConfiguration.radiationDestructionEventLevel && random.nextFloat() < RadiationConfiguration.destructionEventChance) {
                            handleDestructionEvent(world, coordinate, radiationSource);
                        }
                    }
                }
            }
        }

        if (dirty) {
            for (GlobalCoordinate coordinate : toRemove) {
                radiationManager.deleteRadiationSource(coordinate);
                Logging.logDebug("Removed radiation source at: " + coordinate.getCoordinate().toString() + " (" + coordinate.getDimension() + ")");
            }

            radiationManager.save(entityWorld);
        }
    }

    private void handleDestructionEvent(World world, GlobalCoordinate coordinate, DRRadiationManager.RadiationSource radiationSource) {
        int cx = coordinate.getCoordinate().getX();
        int cy = coordinate.getCoordinate().getY();
        int cz = coordinate.getCoordinate().getZ();
        double centerx = cx;
        double centery = cy;
        double centerz = cz;
        double radius = radiationSource.getRadius();

        double theta = random.nextDouble() * Math.PI * 2.0;
        double phi = random.nextDouble() * Math.PI - Math.PI / 2.0;
        double dist = random.nextDouble() * radius;

        double cosphi = Math.cos(phi);
        double destx = centerx + dist * Math.cos(theta) * cosphi;
        double destz = centerz + dist * Math.sin(theta) * cosphi;
        double desty;
        if (random.nextFloat() > 0.5f) {
            desty = world.getTopSolidOrLiquidBlock((int) destx, (int) destz);
        } else {
            desty = centery + dist * Math.sin(phi);
        }
        Logging.logDebug("Destruction event at: " + destx + "," + desty + "," + destz);

        float baseStrength = radiationSource.getStrength();
        double distanceSq = (centerx-destx) * (centerx-destx) + (centery-desty) * (centery-desty) + (centerz-destz) * (centerz-destz);
        double distance = Math.sqrt(distanceSq);
        float strength = (float) (baseStrength * (radius-distance) / radius);
        QuadTree radiationTree = radiationSource.getRadiationTree(world, cx, cy, cz);
        strength = strength * (float) radiationTree.factor(cx, cy, cz, (int) destx, (int) desty, (int) destz);

        int eventradius = 8;
        int damage;
        float poisonBlockChance;
        float setOnFireChance;

        if (strength > RadiationConfiguration.radiationDestructionEventLevel/2) {
            // Worst destruction event
            damage = 30;
            poisonBlockChance = 0.9f;
            setOnFireChance = 0.03f;
        } else if (strength > RadiationConfiguration.radiationDestructionEventLevel/3) {
            // Moderate
            damage = 5;
            poisonBlockChance = 0.6f;
            setOnFireChance = 0.001f;
        } else if (strength > RadiationConfiguration.radiationDestructionEventLevel/4) {
            // Minor
            damage = 1;
            poisonBlockChance = 0.3f;
            setOnFireChance = 0.0f;
        } else {
            return;
        }

        List list = world.selectEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(destx - eventradius, desty - eventradius, destz - eventradius, destx + eventradius, desty + eventradius, destz + eventradius), null);
        for (Object o : list) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) o;
            entityLivingBase.addPotionEffect(new PotionEffect(Potion.harm.getId(), 10, damage));
        }

        for (int x = (int) (destx-eventradius); x <= destx+eventradius ; x++) {
            for (int y = (int) (desty-eventradius); y <= desty+eventradius ; y++) {
                for (int z = (int) (destz-eventradius); z <= destz+eventradius ; z++) {
                    double dSq = (x-destx) * (x-destx) + (y-desty) * (y-desty) + (z-destz) * (z-destz);
                    double d = Math.sqrt(dSq);
                    double str = (eventradius-d) / eventradius;

                    Block block = world.getBlock(x, y, z);
                    if (block == Blocks.dirt || block == Blocks.farmland || block == Blocks.grass) {
                        if (random.nextFloat() < poisonBlockChance * str) {
                            world.setBlock(x, y, z, ModBlocks.poisonedDirtBlock, 0, 2);
                        }
                    } else if (block.isLeaves(world, x, y, z)) {
                        if (random.nextFloat() < poisonBlockChance * str) {
                            world.setBlockToAir(x, y, z);
                        }
                    }
                    if (random.nextFloat() < setOnFireChance * str) {
                        if ((!world.isAirBlock(x, y, z)) && world.isAirBlock(x, y+1, z)) {
                            Logging.logDebug("Set fire at: " + x + "," + y + "," + z);
                            world.setBlock(x, y+1, z, Blocks.fire, 0, 3);
                        }
                    }
                }
            }
        }
    }

    private void handleRadiationEffects(World world, GlobalCoordinate coordinate, DRRadiationManager.RadiationSource radiationSource) {
        int cx = coordinate.getCoordinate().getX();
        int cy = coordinate.getCoordinate().getY();
        int cz = coordinate.getCoordinate().getZ();
        double centerx = cx;
        double centery = cy;
        double centerz = cz;
        double radius = radiationSource.getRadius();
        double radiusSq = radius * radius;
        float baseStrength = radiationSource.getStrength();

        List list = world.selectEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(centerx - radius, centery - radius, centerz - radius, centerx + radius, centery + radius, centerz + radius), null);
        for (Object o : list) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) o;

            int pieces = ItemRadiationSuit.countSuitPieces(entityLivingBase);
            float protection = RadiationConfiguration.suitProtection[pieces];

            double distanceSq = entityLivingBase.getDistanceSq(centerx, centery, centerz);

            if (distanceSq < radiusSq) {
                double distance = Math.sqrt(distanceSq);
                QuadTree radiationTree = radiationSource.getRadiationTree(world, cx, cy, cz);
                float strength = (float) (baseStrength * (radius-distance) / radius);
                strength = strength * (1.0f-protection);
                strength = strength * (float) radiationTree.factor(cx, cy, cz, (int) entityLivingBase.posX, (int) entityLivingBase.posY, (int) entityLivingBase.posZ);

                if (strength < RadiationConfiguration.radiationStrenghLevel0) {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 1, true));
                } else if (strength < RadiationConfiguration.radiationStrenghLevel1) {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), EFFECTS_MAX * MAXTICKS, 1, true));
                } else if (strength < RadiationConfiguration.radiationStrenghLevel2) {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.weakness.getId(), EFFECTS_MAX * MAXTICKS, 1, true));
                } else if (strength < RadiationConfiguration.radiationStrenghLevel3) {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.weakness.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.poison.getId(), EFFECTS_MAX * MAXTICKS, 1, true));
                } else if (strength < RadiationConfiguration.radiationStrenghLevel4) {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.weakness.getId(), EFFECTS_MAX * MAXTICKS, 3, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.poison.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                } else {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.weakness.getId(), EFFECTS_MAX * MAXTICKS, 3, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.poison.getId(), EFFECTS_MAX * MAXTICKS, 3, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.wither.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                }
            }
        }

    }

}
