package mcjty.deepresonance.fluid;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 2-8-2015.
 */
public class DRFluidRegistry {

    public static final Fluid liquidCrystal = new Fluid("liquidCrystal");

    public static void preInitFluids(){
        FluidRegistry.registerFluid(liquidCrystal);
    }

    //TODO: icons for the fluid
    public static void registerIcons(IIconRegister iconRegister){
        liquidCrystal.setIcons(iconRegister.registerIcon(DeepResonance.MODID + ":rclfluid"), iconRegister.registerIcon(DeepResonance.MODID + ":rclfluid"));
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
}
