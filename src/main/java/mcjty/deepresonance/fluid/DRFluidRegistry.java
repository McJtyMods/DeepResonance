package mcjty.deepresonance.fluid;

import elec332.core.client.IIconRegistrar;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 2-8-2015.
 */
public class DRFluidRegistry {


    private static ResourceLocation rclFluid = new ResourceLocation(DeepResonance.MODID + ":rclfluid");
    public static Fluid liquidCrystal = new Fluid("liquidCrystal", rclFluid, rclFluid);

    public static void initFluids(){
        FluidRegistry.registerFluid(liquidCrystal);
    }

    public static void registerIcons(IIconRegistrar iconRegister){
        //rclFluid = iconRegister.registerSprite(new ResourceLocation(DeepResonance.MODID + ":rclfluid"));
    }

    public static boolean isValidLiquidCrystalStack(FluidStack stack){
        return getFluidFromStack(stack) == liquidCrystal;
    }

    public static Fluid getFluidFromStack(FluidStack stack){
        return stack == null ? null : stack.getFluid();
    }

    public static String getFluidName(FluidStack stack){
        Fluid fluid = getFluidFromStack(stack);
        return getFluidName(fluid);
    }

    public static String getFluidName(Fluid fluid){
        return fluid == null ? "null" : fluid.getName();
    }

    public static int getAmount(FluidStack stack){
        return stack == null ? 0 : stack.amount;
    }
}
