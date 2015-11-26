package mcjty.deepresonance.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.tank.TankSetup;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import net.minecraftforge.fluids.FluidRegistry;

@SideOnly(Side.CLIENT)
public class ReturnTankInfoHelper {
    public static void setEnergyLevel(PacketReturnTankInfo message) {
        TankSetup.tank.totalFluidAmount = message.getAmount();
        TankSetup.tank.tankCapacity = message.getCapacity();
        TankSetup.tank.fluidData = LiquidCrystalFluidTagData.fromNBT(message.getTag());
        TankSetup.tank.clientRenderFluid = FluidRegistry.getFluid(message.getFluidName());
    }

}
