package mcjty.deepresonance.network;

import mcjty.deepresonance.blocks.tank.TankSetup;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReturnTankInfoHelper {
    public static void setEnergyLevel(PacketReturnTankInfo message) {
        TankSetup.tank.totalFluidAmount = message.getAmount();
        TankSetup.tank.tankCapacity = message.getCapacity();
        TankSetup.tank.fluidData = LiquidCrystalFluidTagData.fromNBT(message.getTag());
        TankSetup.tank.clientRenderFluid = FluidRegistry.getFluid(message.getFluidName());
    }

}
