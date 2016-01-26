package mcjty.deepresonance.network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.generator.GeneratorBlock;

@SideOnly(Side.CLIENT)
public class ReturnGeneratorInfoHelper {
    public static void setEnergyLevel(PacketReturnGeneratorInfo message) {
        GeneratorBlock.tooltipEnergy = message.getEnergy();
        GeneratorBlock.tooltipRefCount = message.getRefcount();
        GeneratorBlock.tooltipRfPerTick = message.getRfPerTick();
    }

}
