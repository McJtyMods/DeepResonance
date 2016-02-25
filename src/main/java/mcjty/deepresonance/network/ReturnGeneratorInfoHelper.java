package mcjty.deepresonance.network;

import mcjty.deepresonance.blocks.generator.GeneratorBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReturnGeneratorInfoHelper {
    public static void setEnergyLevel(PacketReturnGeneratorInfo message) {
        GeneratorBlock.tooltipEnergy = message.getEnergy();
        GeneratorBlock.tooltipRefCount = message.getRefcount();
        GeneratorBlock.tooltipRfPerTick = message.getRfPerTick();
    }

}
