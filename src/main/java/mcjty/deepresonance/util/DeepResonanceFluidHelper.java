package mcjty.deepresonance.util;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 15-8-2020
 */
public class DeepResonanceFluidHelper {

    public static boolean isValidLiquidCrystalStack(FluidStack stack) {
        return stack != null && isLiquidCrystal(stack.getRawFluid()); //Stack might have size 0
    }

    public static boolean isLiquidCrystal(Fluid fluid) {
        return fluid == CoreModule.LIQUID_CRYSTAL.get();
    }

    public static ILiquidCrystalData readCrystalDataFromNBT(CompoundNBT tag, int amount) {
        return LiquidCrystalData.fromNBT(tag, amount);
    }

    public static FluidStack makeLiquidCrystalStack(int amount) {
        return LiquidCrystalData.makeLiquidCrystalStack(amount, 0, 0, 0, 0);
    }

    public static FluidStack makeLiquidCrystalStack(int amount, float quality, float purity, float strength, float efficiency) {
        return LiquidCrystalData.makeLiquidCrystalStack(amount, quality, purity, strength, efficiency);
    }

    public static ILiquidCrystalData readCrystalDataFromStack(FluidStack stack) {
        return LiquidCrystalData.fromStack(stack);
    }

}
