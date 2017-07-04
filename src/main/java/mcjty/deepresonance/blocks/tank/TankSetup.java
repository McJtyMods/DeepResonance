package mcjty.deepresonance.blocks.tank;

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
//        GameRegistry.addRecipe(new ItemStack(tank), "iii", "ggg", "ipi",  'g', Blocks.GLASS, 'p', ModItems.resonatingPlateItem,
//                'i', Items.IRON_INGOT);
    }
}
