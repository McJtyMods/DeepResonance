package mcjty.deepresonance.modules.radiation.manager;

import com.google.common.collect.Sets;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.radiation.item.ItemRadiationSuit;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;

public class RadiationTickEvent {

    public static final int MAXTICKS = 10;
    private int counter = MAXTICKS;
    private static final Random random = new Random();

    private static final int EFFECTS_MAX = 18;
    private int counterEffects = EFFECTS_MAX;

    public static MobEffect harm;
    public static MobEffect hunger;
    public static MobEffect moveSlowdown;
    public static MobEffect weakness;
    public static MobEffect poison;
    public static MobEffect wither;

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent evt) {
        if (evt.phase == TickEvent.Phase.START) {
            return;
        }
        if (!Objects.equals(Level.OVERWORLD, evt.world.dimension())) {
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

    private void serverTick(Level entityWorld, boolean doEffects) {
//        // @todo improve
//        MinecraftServer server = FMLServerHandler.instance().getServer();
//        if (server == null) {
//            return;
//        }
//        World entityWorld = server.getEntityWorld();
        DRRadiationManager radiationManager = DRRadiationManager.getManager(entityWorld);

        Set<GlobalPos> toRemove = Sets.newHashSet();
        boolean dirty = false;

        for (Map.Entry<GlobalPos, DRRadiationManager.RadiationSource> source : radiationManager.getRadiationSources().entrySet()) {
            GlobalPos coordinate = source.getKey();
            Level world = LevelTools.getLevel(entityWorld, coordinate.dimension());
            if (world != null) {
                if (LevelTools.isLoaded(world, coordinate.pos())) {
                    // The world is loaded and the chunk containing the radiation source is also loaded.
                    DRRadiationManager.RadiationSource radiationSource = source.getValue();
                    float strength = radiationSource.getStrength();

                    strength -= RadiationConfiguration.STRENGTH_DECREASE_TICK.get() * MAXTICKS;
                    dirty = true;
                    if (strength <= 0) {
                        toRemove.add(coordinate);
                    } else {
                        radiationSource.setStrength(strength);

                        if (doEffects) {
                            handleRadiationEffects(world, coordinate, radiationSource);
                        }

                        if (strength > RadiationConfiguration.RADIATION_DESTRUCTION_EVENT_LEVEL.get() && random.nextFloat() < RadiationConfiguration.RADIATION_DESTRUCTION_EVENT_CHANCE.get()) {
                            handleDestructionEvent(world, coordinate, radiationSource);
                        }
                    }
                }
            }
        }

        if (dirty) {
            for (GlobalPos coordinate : toRemove) {
                radiationManager.deleteRadiationSource(coordinate);
                Logging.logDebug("Removed radiation source at: " + coordinate.pos() + " (" + coordinate.dimension() + ")");
            }

            radiationManager.save();
        }
    }

    private void handleDestructionEvent(Level world, GlobalPos coordinate, DRRadiationManager.RadiationSource radiationSource) {
        int cx = coordinate.pos().getX();
        int cy = coordinate.pos().getY();
        int cz = coordinate.pos().getZ();
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
            desty = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, (int)destx, (int)destz);   // @todo check
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

        if (strength > RadiationConfiguration.RADIATION_DESTRUCTION_EVENT_LEVEL.get()/2) {
            // Worst destruction event
            damage = 30;
            poisonBlockChance = 0.9f;
            removeLeafChance = 9.0f;
            setOnFireChance = 0.03f;
        } else if (strength > RadiationConfiguration.RADIATION_DESTRUCTION_EVENT_LEVEL.get()/3) {
            // Moderate
            damage = 5;
            poisonBlockChance = 0.6f;
            removeLeafChance = 4.0f;
            setOnFireChance = 0.001f;
        } else if (strength > RadiationConfiguration.RADIATION_DESTRUCTION_EVENT_LEVEL.get()/4) {
            // Minor
            damage = 1;
            poisonBlockChance = 0.3f;
            removeLeafChance = 1.2f;
            setOnFireChance = 0.0f;
        } else {
            return;
        }

        AABB area = new AABB(destx - eventradius, desty - eventradius, destz - eventradius, destx + eventradius, desty + eventradius, destz + eventradius);
        List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity entityLivingBase : list) {
            getPotions();
            entityLivingBase.addEffect(new MobEffectInstance(harm, 10, damage));
        }

        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
        for (int x = (int) (destx-eventradius); x <= destx+eventradius ; x++) {
            for (int y = (int) (desty-eventradius); y <= desty+eventradius ; y++) {
                for (int z = (int) (destz-eventradius); z <= destz+eventradius ; z++) {
                    double dSq = (x-destx) * (x-destx) + (y-desty) * (y-desty) + (z-destz) * (z-destz);
                    double d = Math.sqrt(dSq);
                    double str = (eventradius-d) / eventradius;
                    currentPos = currentPos.set(x, y, z);

                    Block block = world.getBlockState(currentPos).getBlock();
                    // @todo 1.16 use tags?
                    if (block == Blocks.DIRT || block == Blocks.FARMLAND || block == Blocks.GRASS) {
                        if (random.nextFloat() < poisonBlockChance * str) {
                            world.setBlock(currentPos, RadiationModule.POISONED_DIRT_BLOCK.get().defaultBlockState(), Block.UPDATE_NEIGHBORS);
                        }
                        // @todo 1.16 tags
//                    } else if (block.isLeaves(world.getBlockState(currentPos), world, currentPos) || block instanceof IPlantable) {
//                        if (random.nextFloat() < removeLeafChance * str) {
//                            world.setBlockToAir(currentPos);
//                        }
                    }
                    if (random.nextFloat() < setOnFireChance * str) {
                        // @todo temporarily disabled fire because it causes 'TickNextTick list out of synch' for some reason
//                        if ((!world.isAirBlock(currentPos))){
//                            currentPos.set(x, y+1, z);
//                            if(world.isAirBlock(currentPos)) {
//                                Logging.logDebug("Set fire at: " + x + "," + y + "," + z);
//                                System.out.println("RadiationTickEvent.handleDestructionEvent: FIRE");
//                                System.out.flush();
//                                world.setBlockState(currentPos, Blocks.fire.getDefaultState(), 2);
//                            }
//                        }
                    }
                }
            }
        }
    }

    private static void getPotions() {
        IForgeRegistry<MobEffect> potions = ForgeRegistries.MOB_EFFECTS;
        if (harm == null) {
            harm = potions.getValue(new ResourceLocation("instant_damage"));
            hunger = potions.getValue(new ResourceLocation("hunger"));
            moveSlowdown = potions.getValue(new ResourceLocation("slowness"));
            weakness = potions.getValue(new ResourceLocation("weakness"));
            poison = potions.getValue(new ResourceLocation("poison"));
            wither = potions.getValue(new ResourceLocation("wither"));
        }
    }

    private void handleRadiationEffects(Level world, GlobalPos coordinate, DRRadiationManager.RadiationSource radiationSource) {
        int cx = coordinate.pos().getX();
        int cy = coordinate.pos().getY();
        int cz = coordinate.pos().getZ();
        double centerx = cx;
        double centery = cy;
        double centerz = cz;
        double radius = radiationSource.getRadius();
        double radiusSq = radius * radius;
        float baseStrength = radiationSource.getStrength();

        AABB area = new AABB(centerx - radius, centery - radius, centerz - radius, centerx + radius, centery + radius, centerz + radius);
        List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, area, livingEntity -> true);
        for (LivingEntity entityLivingBase : list) {

            float protection = ItemRadiationSuit.getRadiationProtection(entityLivingBase);

            double distanceSq = entityLivingBase.blockPosition().distSqr(centerx, centery, centerz, true);

            if (distanceSq < radiusSq) {
                double distance = Math.sqrt(distanceSq);
                QuadTree radiationTree = radiationSource.getRadiationTree(world, cx, cy, cz);
                float strength = (float) (baseStrength * (radius-distance) / radius);
                strength = strength * (1.0f-protection);
                strength = strength * (float) radiationTree.factor2(cx, cy, cz, (int) entityLivingBase.position().x, (int) entityLivingBase.position().y+1, (int) entityLivingBase.position().z);
                getPotions();

                if (strength < RadiationConfiguration.RADIATION_EFFECT_LEVEL_0.get()) {
                } else if (strength < RadiationConfiguration.RADIATION_EFFECT_LEVEL_0.get()) {
                    entityLivingBase.addEffect(new MobEffectInstance(hunger, EFFECTS_MAX * MAXTICKS, 0, true, true));
                } else if (strength < RadiationConfiguration.RADIATION_EFFECT_LEVEL_1.get()) {
                    entityLivingBase.addEffect(new MobEffectInstance(hunger, EFFECTS_MAX * MAXTICKS, 1, true, true));
                } else if (strength < RadiationConfiguration.RADIATION_EFFECT_LEVEL_2.get()) {
                    entityLivingBase.addEffect(new MobEffectInstance(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(moveSlowdown, EFFECTS_MAX * MAXTICKS, 1, true, true));
                } else if (strength < RadiationConfiguration.RADIATION_EFFECT_LEVEL_3.get()) {
                    entityLivingBase.addEffect(new MobEffectInstance(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(moveSlowdown, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(weakness, EFFECTS_MAX * MAXTICKS, 1, true, true));
                } else if (strength < RadiationConfiguration.RADIATION_EFFECT_LEVEL_4.get()) {
                    entityLivingBase.addEffect(new MobEffectInstance(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(moveSlowdown, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(weakness, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(poison, EFFECTS_MAX * MAXTICKS, 1, true, true));
                } else if (strength < RadiationConfiguration.RADIATION_EFFECT_LEVEL_5.get()) {
                    entityLivingBase.addEffect(new MobEffectInstance(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(moveSlowdown, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(weakness, EFFECTS_MAX * MAXTICKS, 3, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(poison, EFFECTS_MAX * MAXTICKS, 2, true, true));
                } else {
                    entityLivingBase.addEffect(new MobEffectInstance(hunger, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(moveSlowdown, EFFECTS_MAX * MAXTICKS, 2, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(weakness, EFFECTS_MAX * MAXTICKS, 3, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(poison, EFFECTS_MAX * MAXTICKS, 3, true, true));
                    entityLivingBase.addEffect(new MobEffectInstance(wither, EFFECTS_MAX * MAXTICKS, 2, true, true));
                }
            }
        }

    }

}
