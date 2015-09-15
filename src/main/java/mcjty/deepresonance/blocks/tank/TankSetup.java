package mcjty.deepresonance.blocks.tank;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.deepresonance.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TankSetup {
    public static BlockTank tank;

    public static void setupBlocks() {
        tank = new BlockTank("tankBlock");
        tank.registerTile().register();
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(tank), "iii", "ggg", "ipi",  'g', Blocks.glass, 'p', ModItems.resonatingPlateItem,
                'i', Items.iron_ingot);
    }
}
