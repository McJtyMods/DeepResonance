package mcjty.deepresonance.fluid;

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
        liquidCrystal.setIcons(iconRegister.registerIcon("FluidStillIcon"), iconRegister.registerIcon("FluidFlowingIcon.png"));
    }

    public static boolean isValidLiquidCrystalStack(FluidStack stack){
        return !(stack == null || stack.getFluid() == null || stack.amount == 0 || stack.getFluid() != DRFluidRegistry.liquidCrystal);
    }
}
