package mcjty.deepresonance.items.rftoolsmodule;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.radiationsensor.RadiationSensorBlock;
import mcjty.deepresonance.items.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
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

    public static void initCrafting() {
        GameRegistry.addRecipe(new ItemStack(RFToolsSupport.radiationSensorBlock), "qcq", "tot", "qrq", 'r', Items.redstone, 'q', Items.quartz, 'o', ModBlocks.machineFrame,
                               'c', Items.clock, 't', Items.compass);
        ItemStack inkSac = new ItemStack(Items.dye, 1, 0);
        GameRegistry.addRecipe(new ItemStack(RFToolsSupport.radiationModuleItem), " c ", "rir", " b ", 'c', Items.ender_pearl, 'r', ModItems.resonatingPlateItem, 'i', Items.iron_ingot,
                               'b', inkSac);
    }
}
