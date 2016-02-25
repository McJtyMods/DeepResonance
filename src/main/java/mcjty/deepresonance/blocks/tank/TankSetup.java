package mcjty.deepresonance.blocks.tank;

import mcjty.deepresonance.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TankSetup {

    public static BlockTank tank;

    public static void setupBlocks() {
        tank = new BlockTank();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        tank.initModel();
    }


    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(tank), "iii", "ggg", "ipi",  'g', Blocks.glass, 'p', ModItems.resonatingPlateItem,
                'i', Items.iron_ingot);
    }
}
