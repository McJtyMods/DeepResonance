package mcjty.deepresonance.modules.core.util;

import com.google.common.base.Preconditions;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.util.Constants;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Elec332 on 18-1-2020
 */
public class CrystalHelper {

    public static boolean isEmpty(float power) {
        return power < Constants.CRYSTAL_MIN_POWER;
    }

    public static boolean isVeryPure(float purity) {
        return purity > Constants.PURE_MODEL_THRESHOLD;
    }

    public static int getRfPerTick(float efficiency, float purity) {
        return (int) (CrystalConfig.MAX_POWER_TICK.get() * efficiency / 100.1f * (purity + 2.0f) / 102.0f + 1);
    }

    public static float getTotalPower(float strength, float purity) {
        return 1000.0f * CrystalConfig.MAX_POWER_STORED.get() * strength / 100.0f * (purity + 30.0f) / 130.0f;
    }

    // Special == 0, normal
    // Special == 1, average random
    // Special == 2, best random
    // Special == 3, best non-overcharged
    // Special == 4, almost depleted
    public static void spawnRandomCrystal(World world, Random random, BlockPos pos, int special) {
        float strength;
        float power;
        float efficiency;
        float purity;

        if (special >= 5) {
            strength = 1;
            power = 0.05f;
            efficiency = 1;
            purity = 100;
        } else if (special >= 3) {
            strength = power = efficiency = 100;
            purity = special == 4 ? 1 : 100;
        } else {
            strength = getRandomSpecial(random, special) * 3.0f + 0.01f;
            power = getRandomSpecial(random, special) * 60.0f + 0.2f;
            efficiency = getRandomSpecial(random, special) * 3.0f + 0.1f;
            purity = getRandomSpecial(random, special) * 10.0f + 5.0f;
        }

        spawnCrystal(world, pos, strength, power, efficiency, purity);
    }

    public static void spawnRandomCrystal(World world, Random random, BlockPos pos, float str, float pow, float eff, float pur) {
        spawnCrystal(world, pos,
                Math.min(100.0f, random.nextFloat() * pur * 10.0f + 5.0f),
                Math.min(100.0f, random.nextFloat() * str * 3.0f + 0.01f),
                Math.min(100.0f, random.nextFloat() * eff * 3.0f + 0.1f),
                Math.min(100.0f, random.nextFloat() * pow * 60.0f + 0.2f));
    }

    public static void spawnCrystal(World world, BlockPos pos, float purity, float strength, float efficiency, float power) {
        world.setBlockState(pos, Preconditions.checkNotNull(CoreModule.RESONATING_CRYSTAL_BLOCK.get()).getDefaultState(), 3);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityResonatingCrystal) {
            TileEntityResonatingCrystal resonatingCrystalTileEntity = (TileEntityResonatingCrystal) te;
//            resonatingCrystalTileEntity.setPurity(purity);
//            resonatingCrystalTileEntity.setStrength(strength);
//            resonatingCrystalTileEntity.setEfficiency(efficiency);
//            resonatingCrystalTileEntity.setPower(power); todo
        }
    }

    private static float getRandomSpecial(Random random, int special) {
        return special == 0 ? random.nextFloat() : special == 1 ? .5f : 1.0f;
    }

}
