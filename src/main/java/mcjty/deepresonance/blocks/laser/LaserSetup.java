package mcjty.deepresonance.blocks.laser;

import mcjty.deepresonance.blocks.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LaserSetup {
    public static LaserBlock laserBlock;

    public static void setupBlocks() {
        laserBlock = new LaserBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        laserBlock.initModel();
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(laserBlock), "ggg", "eMe", "ddd", 'M', ModBlocks.machineFrame, 'g', Blocks.glass, 'e', Items.emerald, 'd', Items.diamond);
    }
}
