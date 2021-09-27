package mcjty.deepresonance.modules.worldgen.world;

import elec332.core.api.world.IRetroGenFeature;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.modules.worldgen.WorldGenModule;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Created by Elec332 on 10-7-2020
 */
public class ResonantCrystalFeature extends Feature<ResonantCrystalFeatureConfig> implements IRetroGenFeature<ResonantCrystalFeatureConfig> {

    private static final ResourceLocation NAME = new DeepResonanceResourceLocation("resonant_crystal");

    public ResonantCrystalFeature() {
        super(ResonantCrystalFeatureConfig::deserialize);
        setRegistryName(NAME);
    }

    @Override
    public String getName(ResonantCrystalFeatureConfig config) {
        return config.name;
    }

    @Override
    public boolean place(@Nonnull IWorld worldIn, @Nonnull ChunkGenerator<? extends GenerationSettings> generator, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull ResonantCrystalFeatureConfig config) {
        if (rand.nextDouble() < WorldGenModule.config.crystalSpwanChance.get()) {
            return trySpawnCrystal(worldIn, generator, new ChunkPos(pos), rand, config);
        }
        return false;
    }

    private boolean trySpawnCrystal(@Nonnull IWorld world, @Nonnull ChunkGenerator<? extends GenerationSettings> generator, ChunkPos chunkPos, Random random, ResonantCrystalFeatureConfig config) {
        for (int i = 0; i < WorldGenModule.config.crystalSpawnTries.get(); i++) {
            BlockPos pos = chunkPos.getBlock(random.nextInt(16), 0, random.nextInt(16));
            if (WorldHelper.hasCeiling(generator)) {
                pos = new BlockPos(pos.getX(), 60, pos.getZ());
            } else {
                pos = WorldHelper.getTopBlock(world, pos, Heightmap.Type.OCEAN_FLOOR_WG).down();
            }
            BlockPos.Mutable poz = new BlockPos.Mutable(pos);
            while (poz.getY() > 1 && !world.isAirBlock(poz)) {
                poz.move(Direction.DOWN);
            }
            while (poz.getY() > 1 && world.isAirBlock(poz)) {
                poz.move(Direction.DOWN);
            }
            if (world.getBlockState(poz).isSolid()) {
                pos = poz.up();
                if (world.isAirBlock(pos)) {
                    if (WorldGenModule.config.verbose.get()) {
                        DeepResonance.logger.info("Spawned crystal at: " + pos);
                    }
                    spawnRandomCrystal(world, random, pos, config.strength, config.power, config.efficiency, config.purity);
                    return true;
                }
            }
        }
        return false;
    }

    public static void spawnRandomCrystal(IWorld world, Random random, BlockPos pos, float str, float pow, float eff, float pur) {
        world.setBlockState(pos, CoreModule.RESONATING_CRYSTAL_BLOCK.get().getDefaultState(), 3);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityResonatingCrystal) {
            TileEntityResonatingCrystal tile = (TileEntityResonatingCrystal) te;
            tile.setStrength(Math.min(100.0f, random.nextFloat() * str * 3.0f + 0.01f));
            tile.setPower(Math.min(100.0f, random.nextFloat() * pow * 60.0f + 0.2f));
            tile.setEfficiency(Math.min(100.0f, random.nextFloat() * eff * 3.0f + 0.1f));
            tile.setPurity(Math.min(100.0f, random.nextFloat() * pur * 10.0f + 5.0f));
        }
    }

}
