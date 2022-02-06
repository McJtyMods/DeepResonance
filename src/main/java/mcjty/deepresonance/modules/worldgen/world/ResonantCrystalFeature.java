package mcjty.deepresonance.modules.worldgen.world;

import com.mojang.serialization.Codec;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.worldgen.WorldGenModule;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import javax.annotation.Nonnull;
import java.util.Random;

public class ResonantCrystalFeature extends Feature<ResonantCrystalFeatureConfig> {

    public static final ResourceLocation FEATURE_CRYSTAL_ID = new ResourceLocation(DeepResonance.MODID, "resonant_crystal");

    public static PlacedFeature CRYSTAL_CONFIGURED_OVERWORLD;
    public static PlacedFeature CRYSTAL_CONFIGURED_NETHER;
    public static PlacedFeature CRYSTAL_CONFIGURED_END;

    public ResonantCrystalFeature(Codec<ResonantCrystalFeatureConfig> codec) {
        super(codec);
    }

    public static void registerConfiguredFeatures() {
        // @todo 1.16 configure correctly!
        CRYSTAL_CONFIGURED_OVERWORLD = registerPlacedFeature("configured_crystal_overworld", WorldGenModule.CRYSTAL_FEATURE.get()
                        .configured(new ResonantCrystalFeatureConfig(1.0f, 1.0f, 1.0f, 1.0f)),
                CountPlacement.of(1));
        CRYSTAL_CONFIGURED_NETHER = registerPlacedFeature("configured_crystal_nether", WorldGenModule.CRYSTAL_FEATURE.get()
                        .configured(new ResonantCrystalFeatureConfig(1.0f, 1.0f, 1.0f, 1.0f)),
                CountPlacement.of(1));
        CRYSTAL_CONFIGURED_END = registerPlacedFeature("configured_crystal_end", WorldGenModule.CRYSTAL_FEATURE.get()
                        .configured(new ResonantCrystalFeatureConfig(1.0f, 1.0f, 1.0f, 1.0f)),
                CountPlacement.of(1));
    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> PlacedFeature registerPlacedFeature(String registryName, ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers) {
        PlacedFeature placed = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(registryName), feature).placed(placementModifiers);
        return PlacementUtils.register(registryName, placed);
    }

    @Override
    public boolean place(FeaturePlaceContext<ResonantCrystalFeatureConfig> context) {
        if (context.random().nextDouble() < WorldGenConfiguration.CRYSTAL_SPAWN_CHANCE.get()) {
            return trySpawnCrystal(context.level(), context.chunkGenerator(), new ChunkPos(context.origin()), context.random(), context.config());
        }
        return false;
    }

    private boolean trySpawnCrystal(@Nonnull WorldGenLevel world, @Nonnull ChunkGenerator generator, ChunkPos chunkPos, Random random, ResonantCrystalFeatureConfig config) {
        for (int i = 0; i < WorldGenConfiguration.CRYSTAL_SPAWN_TRIES.get(); i++) {
            BlockPos pos = new BlockPos(chunkPos.getMinBlockX() + random.nextInt(16), 0, chunkPos.getMinBlockZ() + random.nextInt(16));
            if (false) { // @todo 1.16 detect if the world has a ceilijng generator.getSettings().) {
                pos = new BlockPos(pos.getX(), 60, pos.getZ());
            } else {
                int y = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, pos.getX(), pos.getZ());
                pos = new BlockPos(pos.getX(), y - 1, pos.getZ());
            }
            BlockPos.MutableBlockPos poz = pos.mutable();
            while (poz.getY() > world.getMinBuildHeight() && !world.getBlockState(poz).isAir()) {
                poz.move(Direction.DOWN);
            }
            while (poz.getY() > world.getMinBuildHeight() && world.getBlockState(poz).isAir()) {
                poz.move(Direction.DOWN);
            }
            if (world.getBlockState(poz).isCollisionShapeFullBlock(world, poz)) {
                pos = poz.above();
                if (world.getBlockState(pos).isAir()) {
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

    public static void spawnRandomCrystal(WorldGenLevel world, Random random, BlockPos pos, float str, float pow, float eff, float pur) {
        world.setBlock(pos, CoreModule.RESONATING_CRYSTAL_NATURAL.get().defaultBlockState(), Block.UPDATE_ALL);
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ResonatingCrystalTileEntity tile) {
            tile.setStrength(Math.min(100.0f, random.nextFloat() * str * 3.0f + 0.01f));
            tile.setPower(Math.min(100.0f, random.nextFloat() * pow * 60.0f + 0.2f));
            tile.setEfficiency(Math.min(100.0f, random.nextFloat() * eff * 3.0f + 0.1f));
            tile.setPurity(Math.min(100.0f, random.nextFloat() * pur * 10.0f + 5.0f));
        }
    }
}
