package mcjty.deepresonance.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import mcjty.container.GenericBlock;
import mcjty.deepresonance.items.manual.GuiDeepResonanceManual;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiProxy implements IGuiHandler {

    /** This is used to keep track of GUIs that we make*/
    private static int modGuiIndex = 0;

    public static final int GUI_MANUAL_MAIN = modGuiIndex++;

    @Override
    public Object getServerGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (guiid == GUI_MANUAL_MAIN) {
            return null;
        }

        Block block = world.getBlock(x, y, z);
        if (block instanceof GenericBlock) {
            GenericBlock genericBlock = (GenericBlock) block;
            TileEntity te = world.getTileEntity(x, y, z);
            return genericBlock.createServerContainer(entityPlayer, te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (guiid == GUI_MANUAL_MAIN) {
            return new GuiDeepResonanceManual();
        }

        Block block = world.getBlock(x, y, z);
        if (block instanceof GenericBlock) {
            GenericBlock genericBlock = (GenericBlock) block;
            TileEntity te = world.getTileEntity(x, y, z);
            return genericBlock.createClientGui(entityPlayer, te);
        }
        return null;
    }
}
