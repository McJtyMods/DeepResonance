package mcjty.deepresonance.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalBlock;

@SideOnly(Side.CLIENT)
public class ReturnCrystalInfoHelper {
    public static void setEnergyLevel(PacketReturnCrystalInfo message) {
        ResonatingCrystalBlock.tooltipPower = message.getPower();
        ResonatingCrystalBlock.tooltipRFTick = message.getRfPerTick();
    }

}
