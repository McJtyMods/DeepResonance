package mcjty.deepresonance.items.rftoolsmodule;

import mcjty.deepresonance.blocks.radiationsensor.RadiationSensorBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RFToolsSupport {

    public static RadiationModuleItem radiationModuleItem;
    public static RadiationSensorBlock radiationSensorBlock;

    public static void initItems() {
        radiationModuleItem = new RadiationModuleItem();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        radiationModuleItem.initModel();
    }

    public static void initBlocks() {
        radiationSensorBlock = new RadiationSensorBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initBlockModels() {
        radiationSensorBlock.initModel();
    }
}
