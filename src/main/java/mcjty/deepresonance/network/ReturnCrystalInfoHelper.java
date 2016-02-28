package mcjty.deepresonance.network;

import mcjty.deepresonance.blocks.crystals.ResonatingCrystalBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReturnCrystalInfoHelper {
    public static void setEnergyLevel(PacketReturnCrystalInfo message) {
        ResonatingCrystalBlock.tooltipPower = message.getPower();
        ResonatingCrystalBlock.tooltipRFTick = message.getRfPerTick();
    }

}
