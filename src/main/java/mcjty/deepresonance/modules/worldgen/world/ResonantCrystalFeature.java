package mcjty.deepresonance.modules.worldgen.world;

import com.mojang.serialization.MapCodec;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

import javax.annotation.Nonnull;

public class ResonantCrystalFeature implements Feature {

    public static final Identifier FEATURE_CRYSTAL_ID = Identifier.fromNamespaceAndPath(DeepResonance.MODID, "resonant_crystal");

    public static final MapCodec<ResonantCrystalFeature> CODEC = ResonantCrystalFeatureConfig.CODEC
            .xmap(ResonantCrystalFeature::new, feature -> feature.config);

    private final ResonantCrystalFeatureConfig config;

    public ResonantCrystalFeature(ResonantCrystalFeatureConfig config) {
        this.config = config;
    }

    @Override
    public MapCodec<ResonantCrystalFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        if (random.nextDouble() < WorldGenConfiguration.CRYSTAL_SPAWN_CHANCE.get()) {
            return trySpawnCrystal(level, generator, ChunkPos.containing(origin), random, config);
        }
        return false;
    }

    private boolean trySpawnCrystal(@Nonnull WorldGenLevel world, @Nonnull ChunkGenerator generator, ChunkPos chunkPos, RandomSource random, ResonantCrystalFeatureConfig config) {
        for (int i = 0; i < WorldGenConfiguration.CRYSTAL_SPAWN_TRIES.get(); i++) {
            BlockPos pos = new BlockPos(chunkPos.getMinBlockX() + random.nextInt(16), 0, chunkPos.getMinBlockZ() + random.nextInt(16));
            if (false) { // @todo detect if the world has a ceiling generator.getSettings().) {
                pos = new BlockPos(pos.getX(), 60, pos.getZ());
            } else {
                int y = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, pos.getX(), pos.getZ());
                pos = new BlockPos(pos.getX(), y - 1, pos.getZ());
            }
            BlockPos.MutableBlockPos poz = pos.mutable();
            while (poz.getY() > world.getMinY() && !world.getBlockState(poz).isAir()) {
                poz.move(Direction.DOWN);
            }
            while (poz.getY() > world.getMinY() && world.getBlockState(poz).isAir()) {
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

    public static void spawnRandomCrystal(WorldGenLevel world, RandomSource random, BlockPos pos, float str, float pow, float eff, float pur) {
        world.setBlock(pos, CoreModule.RESONATING_CRYSTAL_NATURAL.block().get().defaultBlockState(), Block.UPDATE_ALL);
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ResonatingCrystalTileEntity tile) {
            tile.setStrength(Math.min(100.0f, random.nextFloat() * str * 3.0f + 0.01f));
            tile.setPower(Math.min(100.0f, random.nextFloat() * pow * 60.0f + 0.2f));
            tile.setEfficiency(Math.min(100.0f, random.nextFloat() * eff * 3.0f + 0.1f));
            tile.setPurity(Math.min(100.0f, random.nextFloat() * pur * 10.0f + 5.0f));
        }
    }
}
