package mcjty.deepresonance.blocks.smelter;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SmelterSetup {
    public static SmelterBlock smelter;

    public static void setupBlocks() {
        smelter = new SmelterBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        smelter.initModel();
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(smelter), "ppp", "iMi", "nnn", 'M', ModBlocks.machineFrame, 'n', Blocks.nether_brick, 'p', ModItems.resonatingPlateItem,
                'i', Items.iron_ingot);
    }
}
