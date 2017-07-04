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
}
