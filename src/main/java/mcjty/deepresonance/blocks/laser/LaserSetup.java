package mcjty.deepresonance.blocks.laser;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.lib.container.GenericItemBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class LaserSetup {
    public static LaserBlock laserBlock;

    public static void setupBlocks() {
        laserBlock = new LaserBlock();
        GameRegistry.registerBlock(laserBlock, GenericItemBlock.class, "laserBlock");
        GameRegistry.registerTileEntity(LaserTileEntity.class, "LaserTileEntity");
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(laserBlock), "ggg", "eMe", "ddd", 'M', ModBlocks.machineFrame, 'g', Blocks.glass, 'e', Items.emerald, 'd', Items.diamond);
    }
}
