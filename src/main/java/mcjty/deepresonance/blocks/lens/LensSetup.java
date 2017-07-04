package mcjty.deepresonance.blocks.lens;

import mcjty.deepresonance.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LensSetup {
    public static LensBlock lensBlock;

    public static void setupBlocks() {
        lensBlock = new LensBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        lensBlock.initModel();
    }


    public static void setupCrafting() {
//        GameRegistry.addRecipe(new ItemStack(lensBlock), "gpg", "pXp", "gpg", 'g', Blocks.GLASS_PANE, 'p', ModItems.resonatingPlateItem, 'X', Items.EMERALD);
    }
}
