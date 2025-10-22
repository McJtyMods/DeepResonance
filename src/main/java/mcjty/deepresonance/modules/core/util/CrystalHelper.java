package mcjty.deepresonance.modules.core.util;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.util.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Random;

public class CrystalHelper {

    public static boolean isEmpty(float power) {
        return power < Constants.CRYSTAL_MIN_POWER;
    }

    public static void spawnCrystal(Level world, Random random, BlockPos pos, CrystalOption type) {
        float strength = 0;
        float power = 0;
        float efficiency = 0;
        float purity = 0;

        switch (type) {
            case DEFAULT:
                strength = 20.0f;
                power = 100.0f;
                efficiency = 20.0f;
                purity = 20.0f;
                break;
            case DEPLETED:
                strength = 20.0f;
                power = 0.001f;
                efficiency = 20.0f;
                purity = 20.0f;
                break;
            case RANDOM:
                strength = random.nextFloat() * 100.0f;
                power = random.nextFloat() * 100.0f;
                efficiency = random.nextFloat() *  100.0f;
                purity = random.nextFloat() *   100.0f;
                break;
            case MAX:
                strength = 100.0f;
                power = 100.0f;
                efficiency = 100.0f;
                purity = 100.0f;
                break;
            case DIRTY:
                strength = 100.0f;
                power = 100.0f;
                efficiency = 100.0f;
                purity = 1.0f;
                break;
        }
        spawnCrystal(world, pos, purity, strength, efficiency, power);
    }

    private static void spawnCrystal(Level world, BlockPos pos, float purity, float strength, float efficiency, float power) {
        world.setBlock(pos, CoreModule.RESONATING_CRYSTAL_GENERATED.block().get().defaultBlockState(), Block.UPDATE_ALL);
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ResonatingCrystalTileEntity crystal) {
            crystal.setPurity(purity);
            crystal.setStrength(strength);
            crystal.setEfficiency(efficiency);
            crystal.setPower(power);
        }
    }

    public enum CrystalOption implements StringRepresentable {
        DEFAULT,
        DEPLETED,
        RANDOM,
        MAX,
        DIRTY;

        @Override
        public String getSerializedName() {
            return getSerializedName();
        }
    }
}
