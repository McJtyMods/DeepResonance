package mcjty.deepresonance.radiation;

import com.google.common.collect.Sets;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.items.armor.ItemRadiationSuit;
import mcjty.deepresonance.varia.QuadTree;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RadiationTickEvent {

    public static final int MAXTICKS = 10;
    private int counter = MAXTICKS;
    private static Random random = new Random();

    private static final int EFFECTS_MAX = 18;
    private int counterEffects = EFFECTS_MAX;

    public static Potion harm;
    public static Potion hunger;
    public static Potion moveSlowdown;
    public static Potion weakness;
    public static Potion poison;
    public static Potion wither;

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent evt) {
        if (evt.phase == TickEvent.Phase.START) {
            return;
        }
        if (evt.world.provider.getDimension() != 0) {
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
            serverTick(evt.world, doEffects);
        }
    }


//    @SubscribeEvent
//    public void onServerTick(TickEvent.ServerTickEvent evt) {
//        if (evt.phase == TickEvent.Phase.START) {
//            return;
//        }
//        counter--;
//        if (counter <= 0) {
//            counter = MAXTICKS;
//
//            counterEffects--;
//            boolean doEffects = false;
//            if (counterEffects <= 0) {
//                counterEffects = EFFECTS_MAX;
//                doEffects = true;
//            }
//            serverTick(doEffects);
//        }
//    }

    private void serverTick(World entityWorld, boolean doEffects) {
//        // @todo improve
//        MinecraftServer server = FMLServerHandler.instance().getServer();
//        if (server == null) {
//            return;
//        }
//        World entityWorld = server.getEntityWorld();
        DRRadiationManager radiationManager = DRRadiationManager.getManager(entityWorld);

        Set<GlobalCoordinate> toRemove = Sets.newHashSet();
        boolean dirty = false;

        for (Map.Entry<GlobalCoordinate, DRRadiationManager.RadiationSource> source : radiationManager.getRadiationSources().entrySet()) {
            GlobalCoordinate coordinate = source.getKey();
            World world = DimensionManager.getWorld(coordinate.getDimension());
            if (world != null) {
                if (WorldHelper.chunkLoaded(world, coordinate.getCoordinate())) {
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
            desty = world.getTopSolidOrLiquidBlock(new BlockPos((int) destx, world.getActualHeight(),(int) destz)).getY();
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
        float removeLeafChance;
        float setOnFireChance;

        if (strength > RadiationConfiguration.radiationDestructionEventLevel/2) {
            // Worst destruction event
            damage = 30;
            poisonBlockChance = 0.9f;
            removeLeafChance = 9.0f;
            setOnFireChance = 0.03f;
        } else if (strength > RadiationConfiguration.radiationDestructionEventLevel/3) {
            // Moderate
            damage = 5;
            poisonBlockChance = 0.6f;
            removeLeafChance = 4.0f;
            setOnFireChance = 0.001f;
        } else if (strength > RadiationConfiguration.radiationDestructionEventLevel/4) {
            // Minor
            damage = 1;
            poisonBlockChance = 0.3f;
            removeLeafChance = 1.2f;
            setOnFireChance = 0.0f;
        } else {
            return;
        }

        List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(destx - eventradius, desty - eventradius, destz - eventradius, destx + eventradius, desty + eventradius, destz + eventradius), null);
        for (EntityLivingBase entityLivingBase : list) {
            getPotions();
            entityLivingBase.addPotionEffect(new PotionEffect(harm, 10, damage));
        }

        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
        for (int x = (int) (destx-eventradius); x <= destx+eventradius ; x++) {
            for (int y = (int) (desty-eventradius); y <= desty+eventradius ; y++) {
                for (int z = (int) (destz-eventradius); z <= destz+eventradius ; z++) {
                    double dSq = (x-destx) * (x-destx) + (y-desty) * (y-desty) + (z-destz) * (z-destz);
                    double d = Math.sqrt(dSq);
                    double str = (eventradius-d) / eventradius;
                    currentPos = currentPos.setPos(x, y, z);

                    Block block = WorldHelper.getBlockAt(world, currentPos);
                    if (block == Blocks.DIRT || block == Blocks.FARMLAND || block == Blocks.GRASS) {
                        if (random.nextFloat() < poisonBlockChance * str) {
                            WorldHelper.setBlockState(world, currentPos, ModBlocks.poisonedDirtBlock.getDefaultState(), 2);
                        }
                    } else if (block.isLeaves(world.getBlockState(currentPos), world, currentPos) || block instanceof IPlantable) {
                        if (random.nextFloat() < removeLeafChance * str) {
                            world.setBlockToAir(currentPos);
                        }
                    }
                    if (random.nextFloat() < setOnFireChance * str) {
                        // @todo temporarily disabled fire because it causes 'TickNextTick list out of synch' for some reason
//                        if ((!world.isAirBlock(currentPos))){
//                            currentPos.set(x, y+1, z);
//                            if(world.isAirBlock(currentPos)) {
//                                Logging.logDebug("Set fire at: " + x + "," + y + "," + z);
//                                System.out.println("RadiationTickEvent.handleDestructionEvent: FIRE");
//                                System.out.flush();
//                                WorldHelper.setBlockState(world, currentPos, Blocks.fire.getDefaultState(), 2);
//                            }
//                        }
                    }
                }
            }
        }
    }

    private static void getPotions() {
        IForgeRegistry<Potion> potionRegistry = ForgeRegistries.POTIONS;
        if (harm == null) {
            harm = potionRegistry.getValue(new ResourceLocation("instant_damage"));
            hunger = potionRegistry.getValue(new ResourceLocation("hunger"));
            moveSlowdown = potionRegistry.getValue(new ResourceLocation("slowness"));
            weakness = potionRegistry.getValue(new ResourceLocation("weakness"));
            poison = potionRegistry.getValue(new ResourceLocation("poison"));
            wither = potionRegistry.getValue(new ResourceLocation("wither"));
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

        List<EntityLivingBase> list  = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(centerx - radius, centery - radius, centerz - radius, centerx + radius, centery + radius, centerz + radius), null);
        for (EntityLivingBase entityLivingBase : list) {

            float protection = ItemRadiationSuit.getRadiationProtection(entityLivingBase);

            double distanceSq = entityLivingBase.getDistanceSq(centerx, centery, centerz);

            if (distanceSq < radiusSq) {
                double distance = Math.sqrt(distanceSq);
                QuadTree radiationTree = radiationSource.getRadiationTree(world, cx, cy, cz);
                float strength = (float) (baseStrength * (radius-distance) / radius);
                strength = strength * (1.0f-protection);
                strength = strength * (float) radiationTree.factor2(cx, cy, cz, (int) entityLivingBase.posX, (int) entityLivingBase.posY+1, (int) entityLivingBase.posZ);
                getPotions();

                if (strength < RadiationConfiguration.radiationEffectLevelNone) {
                } else if (strength < RadiationConfiguration.radiationEffectLevel0) {
                    entityLivingBase.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX * MAXTICKS, 0, true, true));
                } else if (strength < RadiationConfiguration.radiationEffectLevel1) {
                    entityLivingBase.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX * MAXTICKS, 1, true, true));
                } else if (strength < RadiationConfiguration.radiationEffectLevel2) {
                    entityLivingBase.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX * MAXTICKS, 1, true, true));
                } else if (strength < RadiationConfiguration.radiationEffectLevel3) {
                    entityLivingBase.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(weakness, EFFECTS_MAX * MAXTICKS, 1, true, true));
                } else if (strength < RadiationConfiguration.radiationEffectLevel4) {
                    entityLivingBase.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(weakness, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(poison, EFFECTS_MAX * MAXTICKS, 1, true, true));
                } else if (strength < RadiationConfiguration.radiationEffectLevel5) {
                    entityLivingBase.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(weakness, EFFECTS_MAX * MAXTICKS, 3, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(poison, EFFECTS_MAX * MAXTICKS, 2, true, true));
                } else {
                    entityLivingBase.addPotionEffect(new PotionEffect(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(moveSlowdown, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(weakness, EFFECTS_MAX * MAXTICKS, 3, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(poison, EFFECTS_MAX * MAXTICKS, 3, true, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(wither, EFFECTS_MAX * MAXTICKS, 2, true, true));
                }
            }
        }

    }

}
