package mcjty.deepresonance.fluid;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created by Elec332 on 2-8-2015.
 */
public class DRFluidRegistry {

    public static final Fluid testFluid = new Fluid("fluidname");

    public static void preInitFluids(){
        FluidRegistry.registerFluid(testFluid);
    }

    public static void registerIcons(IIconRegister iconRegister){
        testFluid.setIcons(iconRegister.registerIcon("FluidStillIcon"), iconRegister.registerIcon("FluidFlowingIcon.png"));
    }
}
