package mcjty.deepresonance.modules.radiation.manager;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import mcjty.deepresonance.api.radiation.IWorldRadiationManager;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.deepresonance.modules.radiation.util.RadiationHelper;
import mcjty.lib.varia.LevelTools;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class RadiationManager implements IWorldRadiationManager, INBTSerializable<CompoundNBT>, IClearable {

    private static final int EFFECTS_MAX = 12;
    public static final int MAXTICKS = 10;

    private final Map<BlockPos, RadiationSource> sources = Maps.newHashMap();

    private int counterEffects = EFFECTS_MAX;
    private int counter = MAXTICKS;

    @Override
    public void clearContent() {
        sources.clear();
    }

    @Override
    public void removeAllRadiation() {
        sources.clear();
    }

    @Nonnull
    @Override
    public RadiationSource getOrCreateRadiationSource(BlockPos coordinate) {
        RadiationSource source = getRadiationSource(coordinate);
        if (source == null) {
            source = new RadiationSource();
            sources.put(coordinate, source);
        }
        return source;
    }

    @Override
    public RadiationSource getRadiationSource(BlockPos coordinate) {
        return sources.get(coordinate);
    }

    public void deleteRadiationSource(BlockPos coordinate) {
        sources.remove(coordinate);
    }

    void tick(World world) {
        counter--;
        if (counter <= 0) {
            counter = MAXTICKS;
            counterEffects--;
            boolean effects = counterEffects <= 0;
            if (effects) {
                counterEffects = EFFECTS_MAX;
            }
            doTick(world, effects);
        }
    }

    private void doTick(World world, boolean effects) {
        Set<BlockPos> toRemove = Sets.newHashSet();

        for (Map.Entry<BlockPos, RadiationSource> source : sources.entrySet()) {
            BlockPos coordinate = source.getKey();
            if (LevelTools.isLoaded(world, coordinate)) {
                // The world is loaded and the chunk containing the radiation source is also loaded.
                RadiationSource radiationSource = source.getValue();
                float strength = radiationSource.getStrength();

                strength -= RadiationConfiguration.STRENGTH_DECREASE_TICK.get() * MAXTICKS;
                if (strength <= 0) {
                    toRemove.add(coordinate);
                } else {
                    radiationSource.setStrength(strength);

                    if (effects) {
                        handleRadiationEffects(world, coordinate, radiationSource);
                    }

                    if (strength > RadiationConfiguration.RADIATION_DESTRUCTION_EVENT_LEVEL.get() && world.getRandom().nextFloat() < RadiationConfiguration.RADIATION_DESTRUCTION_EVENT_CHANCE.get()) {
                        handleDestructionEvent(world, coordinate, radiationSource);
                    }
                }
            }
        }

        for (BlockPos coordinate : toRemove) {
            deleteRadiationSource(coordinate);
        }

    }

    private void handleDestructionEvent(World world, BlockPos coordinate, RadiationSource radiationSource) {
        Random random = world.getRandom();
        int cx = coordinate.getX();
        int cy = coordinate.getY();
        int cz = coordinate.getZ();
        double radius = radiationSource.getRadius();

        double theta = random.nextDouble() * Math.PI * 2.0;
        double phi = random.nextDouble() * Math.PI - Math.PI / 2.0;
        double dist = random.nextDouble() * radius;

        double cosphi = Math.cos(phi);
        double destx = cx + dist * Math.cos(theta) * cosphi;
        double destz = cz + dist * Math.sin(theta) * cosphi;
        double desty;
        if (random.nextFloat() > 0.5f) {
            desty = world.getHeight(Heightmap.Type.WORLD_SURFACE, (int)destx, (int)destz);
        } else {
            desty = cy + dist * Math.sin(phi);
        }

        float baseStrength = radiationSource.getStrength();
        double distanceSq = (cx - destx) * (cx - destx) + (cy - desty) * (cy - desty) + (cz - destz) * (cz - destz);
        double distance = Math.sqrt(distanceSq);
        float strength = (float) (baseStrength * (radius - distance) / radius);
        QuadTree radiationTree = radiationSource.getRadiationTree(world, cx, cy, cz);
        strength = strength * (float) radiationTree.factor(cx, cy, cz, (int) destx, (int) desty, (int) destz);

        int eventradius = 8;
        int damage;
        float poisonBlockChance;
        float removeLeafChance;
        float setOnFireChance;

        if (strength > RadiationConfiguration.RADIATION_DESTRUCTION_EVENT_LEVEL.get() / 2) {
            // Worst destruction event
            damage = 30;
            poisonBlockChance = 0.9f;
            removeLeafChance = 9.0f;
            setOnFireChance = 0.03f;
        } else if (strength > RadiationConfiguration.RADIATION_DESTRUCTION_EVENT_LEVEL.get() / 3) {
            // Moderate
            damage = 5;
            poisonBlockChance = 0.6f;
            removeLeafChance = 4.0f;
            setOnFireChance = 0.002f;
        } else if (strength > RadiationConfiguration.RADIATION_DESTRUCTION_EVENT_LEVEL.get() / 4) {
            // Minor
            damage = 1;
            poisonBlockChance = 0.3f;
            removeLeafChance = 1.2f;
            setOnFireChance = 0.0f;
        } else {
            return;
        }

        List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(destx - eventradius, desty - eventradius, destz - eventradius, destx + eventradius, desty + eventradius, destz + eventradius), null);
        for (LivingEntity entityLivingBase : list) {
            entityLivingBase.addEffect(new EffectInstance(Effects.HARM, 10, damage));
        }

        BlockPos.Mutable currentPos = new BlockPos.Mutable();
        for (int x = -eventradius; x <= eventradius; x++) {
            for (int y = -eventradius; y <= eventradius; y++) {
                for (int z = -eventradius; z <= eventradius; z++) {
                    double dSq = x * x + y * y + z * z;
                    double d = Math.sqrt(dSq);
                    double str = (eventradius - d) / eventradius;
                    currentPos.set(x + destx, y + desty, z + destz);
                    Block block = world.getBlockState(currentPos).getBlock();
                    if (Tags.Blocks.DIRT.contains(block) || block == Blocks.FARMLAND) {
                        if (random.nextFloat() < poisonBlockChance * str) {
                            world.setBlock(currentPos, RadiationModule.POISONED_DIRT_BLOCK.get().defaultBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
                        }
                    } else if (BlockTags.LEAVES.contains(block) || block instanceof IPlantable) {
                        if (random.nextFloat() < removeLeafChance * str) {
                            world.setBlock(currentPos, Blocks.AIR.defaultBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
                        }
                    }
                    if (random.nextFloat() < setOnFireChance * str) {
                        if ((!world.getBlockState(currentPos).isAir())) {
                            currentPos.move(Direction.UP);
                            // @todo 1.16
//                            if (FlintAndSteelItem.canSetFire(WorldHelper.getBlockState(world, currentPos), world, currentPos)) {
//                                BlockItemUseContext context = new BlockItemUseContext()
//                                BlockState blockstate1 = ((FireBlock) Blocks.FIRE).getStateForPlacement(world, currentPos);
//                                world.setBlock(currentPos, blockstate1, 11);
//                            }
                        }
                    }
                }
            }
        }
    }

    private void handleRadiationEffects(World world, BlockPos coordinate, RadiationSource radiationSource) {
        int cx = coordinate.getX();
        int cy = coordinate.getY();
        int cz = coordinate.getZ();
        double radius = radiationSource.getRadius();
        double radiusSq = radius * radius;
        float baseStrength = radiationSource.getStrength();

        List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(cx - radius, cy - radius, cz - radius, cx + radius, cy + radius, cz + radius), null);
        for (LivingEntity entityLivingBase : list) {

            double distanceSq = entityLivingBase.distanceToSqr(cx, cy, cz);

            if (distanceSq < radiusSq) {
                double distance = Math.sqrt(distanceSq);
                QuadTree radiationTree = radiationSource.getRadiationTree(world, cx, cy, cz);
                float strength = (float) (baseStrength * (radius - distance) / radius);
                strength *= 1 - RadiationHelper.getSuitProtection(entityLivingBase, strength);
                strength *= (float) radiationTree.factor2(cx, cy, cz, (int) entityLivingBase.getX(), (int) entityLivingBase.getY() + 1, (int) entityLivingBase.getZ());

                Map<Effect, Integer> effects = Maps.newHashMap();

                if (strength > RadiationConfiguration.RADIATION_EFFECT_LEVEL_5.get()) {
                    effects.put(Effects.HUNGER, 2);
                    effects.put(Effects.MOVEMENT_SLOWDOWN, 2);
                    effects.put(Effects.WEAKNESS, 3);
                    effects.put(Effects.POISON, 3);
                    effects.put(Effects.WITHER, 2);
                } else if (strength > RadiationConfiguration.RADIATION_EFFECT_LEVEL_4.get()) {
                    effects.put(Effects.HUNGER, 2);
                    effects.put(Effects.MOVEMENT_SLOWDOWN, 2);
                    effects.put(Effects.WEAKNESS, 3);
                    effects.put(Effects.POISON, 2);
                } else if (strength > RadiationConfiguration.RADIATION_EFFECT_LEVEL_3.get()) {
                    effects.put(Effects.HUNGER, 2);
                    effects.put(Effects.MOVEMENT_SLOWDOWN, 2);
                    effects.put(Effects.WEAKNESS, 2);
                    effects.put(Effects.POISON, 1);
                } else if (strength > RadiationConfiguration.RADIATION_EFFECT_LEVEL_2.get()) {
                    effects.put(Effects.HUNGER, 2);
                    effects.put(Effects.MOVEMENT_SLOWDOWN, 2);
                    effects.put(Effects.WEAKNESS, 1);
                } else if (strength > RadiationConfiguration.RADIATION_EFFECT_LEVEL_1.get()) {
                    effects.put(Effects.HUNGER, 2);
                    effects.put(Effects.MOVEMENT_SLOWDOWN, 1);
                } else if (strength > RadiationConfiguration.RADIATION_EFFECT_LEVEL_0.get()) {
                    effects.put(Effects.HUNGER, 1);
                } else if (strength > RadiationConfiguration.RADIATION_EFFECT_LEVEL_NONE.get()) {
                    effects.put(Effects.HUNGER, 0);
                }
                effects.forEach((effect, amplifier) -> entityLivingBase.addEffect(new EffectInstance(effect, EFFECTS_MAX * (MAXTICKS + 1), amplifier, true, true)));
            }
        }

    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT ret = new CompoundNBT();
        ListNBT lst = new ListNBT();
        for (Map.Entry<BlockPos, RadiationSource> entry : sources.entrySet()) {
            CompoundNBT tc = new CompoundNBT();
            tc.putLong("coord", entry.getKey().asLong());
            tc.put("radiation_source", entry.getValue().serializeNBT());
            lst.add(tc);
        }
        ret.put("radiation", lst);
        return ret;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        sources.clear();
        ListNBT lst = tag.getList("radiation", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < lst.size(); i++) {
            CompoundNBT tc = lst.getCompound(i);
            BlockPos coordinate = BlockPos.of(tc.getLong("coord"));
            RadiationSource value = new RadiationSource();
            value.deserializeNBT(tc.getCompound("radiation_source"));
            sources.put(coordinate, value);
        }
    }

}
