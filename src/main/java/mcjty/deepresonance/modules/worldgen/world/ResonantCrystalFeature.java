package mcjty.deepresonance.modules.worldgen.world;

import com.mojang.serialization.Codec;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.worldgen.WorldGenModule;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Random;

public class ResonantCrystalFeature extends Feature<ResonantCrystalFeatureConfig> {

    public static final ResourceLocation FEATURE_CRYSTAL_ID = new ResourceLocation(DeepResonance.MODID, "resonant_crystal");
    public static final ResourceLocation OVERWORLD_CONFIGURED_CRYSTAL_ID = new ResourceLocation(DeepResonance.MODID, "configured_crystal_overworld");
    public static final ResourceLocation NETHER_CONFIGURED_CRYSTAL_ID = new ResourceLocation(DeepResonance.MODID, "configured_crystal_nether");
    public static final ResourceLocation END_CONFIGURED_CRYSTAL_ID = new ResourceLocation(DeepResonance.MODID, "configured_crystal_end");

    public static ConfiguredFeature<?, ?> CRYSTAL_CONFIGURED_OVERWORLD;
    public static ConfiguredFeature<?, ?> CRYSTAL_CONFIGURED_NETHER;
    public static ConfiguredFeature<?, ?> CRYSTAL_CONFIGURED_END;

    public ResonantCrystalFeature(Codec<ResonantCrystalFeatureConfig> codec) {
        super(codec);
    }

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

        // @todo 1.16 configure correctly!
        CRYSTAL_CONFIGURED_OVERWORLD = WorldGenModule.CRYSTAL_FEATURE.get()
                .configured(new ResonantCrystalFeatureConfig(1.0f, 1.0f, 1.0f, 1.0f))
                .decorated(Placement.COUNT.configured(new FeatureSpreadConfig(1)));
        CRYSTAL_CONFIGURED_NETHER = WorldGenModule.CRYSTAL_FEATURE.get()
                .configured(new ResonantCrystalFeatureConfig(1.0f, 1.0f, 1.0f, 1.0f))
                .decorated(Placement.COUNT.configured(new FeatureSpreadConfig(1)));
        CRYSTAL_CONFIGURED_END = WorldGenModule.CRYSTAL_FEATURE.get()
                .configured(new ResonantCrystalFeatureConfig(1.0f, 1.0f, 1.0f, 1.0f))
                .decorated(Placement.COUNT.configured(new FeatureSpreadConfig(1)));

        Registry.register(registry, OVERWORLD_CONFIGURED_CRYSTAL_ID, CRYSTAL_CONFIGURED_OVERWORLD);
        Registry.register(registry, NETHER_CONFIGURED_CRYSTAL_ID, CRYSTAL_CONFIGURED_NETHER);
        Registry.register(registry, END_CONFIGURED_CRYSTAL_ID, CRYSTAL_CONFIGURED_END);
    }

    @Override
    public boolean place(@Nonnull ISeedReader reader, @Nonnull ChunkGenerator generator, Random rand, @Nonnull BlockPos pos, @Nonnull ResonantCrystalFeatureConfig config) {
        if (rand.nextDouble() < WorldGenConfiguration.CRYSTAL_SPAWN_CHANCE.get()) {
            return trySpawnCrystal(reader, generator, new ChunkPos(pos), rand, config);
        }
        return false;
    }

    private boolean trySpawnCrystal(@Nonnull ISeedReader world, @Nonnull ChunkGenerator generator, ChunkPos chunkPos, Random random, ResonantCrystalFeatureConfig config) {
        for (int i = 0; i < WorldGenConfiguration.CRYSTAL_SPAWN_TRIES.get(); i++) {
            BlockPos pos = new BlockPos(chunkPos.getMinBlockX() + random.nextInt(16), 0, chunkPos.getMinBlockZ() + random.nextInt(16));
            if (false) { // @todo 1.16 detect if the world has a ceilijng generator.getSettings().) {
                pos = new BlockPos(pos.getX(), 60, pos.getZ());
            } else {
                int y = world.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX(), pos.getZ());
                pos = new BlockPos(pos.getX(), y - 1, pos.getZ());
            }
            BlockPos.Mutable poz = pos.mutable();
            while (poz.getY() > 1 && !world.getBlockState(poz).isAir(world, poz)) {
                poz.move(Direction.DOWN);
            }
            while (poz.getY() > 1 && world.getBlockState(poz).isAir(world, poz)) {
                poz.move(Direction.DOWN);
            }
            if (world.getBlockState(poz).isCollisionShapeFullBlock(world, poz)) {
                pos = poz.above();
                if (world.getBlockState(pos).isAir(world, poz)) {
                    if (WorldGenConfiguration.VERBOSE.get()) {
                        DeepResonance.setup.getLogger().info("Spawned crystal at: " + pos);
                    }
                    spawnRandomCrystal(world, random, pos, config.strength, config.power, config.efficiency, config.purity);
                    return true;
                }
            }
        }
        return false;
    }

    public static void spawnRandomCrystal(ISeedReader world, Random random, BlockPos pos, float str, float pow, float eff, float pur) {
        world.setBlock(pos, CoreModule.RESONATING_CRYSTAL_NATURAL.get().defaultBlockState(),
                Constants.BlockFlags.DEFAULT);
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity tile = (ResonatingCrystalTileEntity) te;
            tile.setStrength(Math.min(100.0f, random.nextFloat() * str * 3.0f + 0.01f));
            tile.setPower(Math.min(100.0f, random.nextFloat() * pow * 60.0f + 0.2f));
            tile.setEfficiency(Math.min(100.0f, random.nextFloat() * eff * 3.0f + 0.1f));
            tile.setPurity(Math.min(100.0f, random.nextFloat() * pur * 10.0f + 5.0f));
        }
    }
}
