package mcjty.deepresonance.blocks.pedestal;

import mcjty.deepresonance.blocks.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PedestalSetup {
    public static PedestalBlock pedestalBlock;

    public static void setupBlocks() {
        pedestalBlock = new PedestalBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        pedestalBlock.initModel();
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(pedestalBlock), "idi", "iMi", "iCi", 'M', ModBlocks.machineFrame, 'r', Items.REDSTONE,
                'd', Blocks.DISPENSER, 'C', Items.COMPARATOR, 'i', Items.IRON_INGOT);
    }
}
