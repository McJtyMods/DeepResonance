package mcjty.deepresonance.blocks.purifier;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.items.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PurifierSetup {
    public static PurifierBlock purifierBlock;

    public static void setupBlocks() {
        purifierBlock = new PurifierBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        purifierBlock.initModel();
    }

    public static void setupCrafting() {
//        GameRegistry.addRecipe(new ItemStack(purifierBlock), "ggg", "iMi", "ggg", 'M', ModBlocks.machineFrame, 'g', ModItems.filterMaterialItem, 'i', Items.IRON_INGOT);
    }
}
