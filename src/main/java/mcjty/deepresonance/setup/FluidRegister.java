package mcjty.deepresonance.setup;

import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.registration.IObjectRegister;
import elec332.core.util.StatCollector;
import mcjty.deepresonance.fluids.FluidLiquidCrystal;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by Elec332 on 7-1-2020
 */
@StaticLoad
public class FluidRegister implements IObjectRegister<Fluid> {

    public static Fluid liquidCrystal;

    public static boolean isValidLiquidCrystalStack(FluidStack stack) {
        return stack != null && stack.getRawFluid() == liquidCrystal; //Stack might have size 0
    }

    public static Fluid getFluidFromStack(FluidStack stack) {
        return stack == null ? null : stack.getFluid();
    }

    public static String getFluidName(FluidStack stack) {
        Fluid fluid = getFluidFromStack(stack);
        return getFluidName(fluid);
    }

    public static String getFluidName(Fluid fluid) {
        return fluid == null ? "null" : StatCollector.translateToLocal(fluid.getAttributes().getTranslationKey());
    }

    public static int getAmount(FluidStack stack) {
        return stack == null ? 0 : stack.getAmount();
    }

    @Override
    public void preRegister() {
        liquidCrystal = new FluidLiquidCrystal();
    }

    @Override
    public void register(IForgeRegistry<Fluid> registry) {
        registry.register(liquidCrystal);
    }

}
