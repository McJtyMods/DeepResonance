package mcjty.deepresonance.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.generator.GeneratorBlock;

@SideOnly(Side.CLIENT)
public class ReturnGeneratorInfoHelper {
    public static void setEnergyLevel(PacketReturnGeneratorInfo message) {
        GeneratorBlock.tooltipEnergy = message.getEnergy();
        GeneratorBlock.tooltipRefCount = message.getRefcount();
        GeneratorBlock.tooltipRfPerTick = message.getRfPerTick();
    }

}
