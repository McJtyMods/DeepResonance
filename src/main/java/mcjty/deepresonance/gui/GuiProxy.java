package mcjty.deepresonance.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import elec332.core.baseclasses.tileentity.IInventoryTile;
import mcjty.deepresonance.items.manual.GuiDeepResonanceManual;
import mcjty.lib.container.GenericBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiProxy implements IGuiHandler {

    /** This is used to keep track of GUIs that we make*/
    private static int modGuiIndex = 0;

    public static final int GUI_MANUAL_MAIN = modGuiIndex++;
    public static final int GUI_SMELTER = modGuiIndex++;
    public static final int GUI_CRYSTALIZER = modGuiIndex++;
    public static final int GUI_PURIFIER = modGuiIndex++;
    public static final int GUI_PEDESTAL = modGuiIndex++;
    public static final int GUI_VALVE = modGuiIndex++;
    public static final int GUI_LASER = modGuiIndex++;
    public static final int ELEC_GUI_STUFF = modGuiIndex++;

    @Override
    public Object getServerGuiElement(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (guiID == GUI_MANUAL_MAIN) {
            return null;
        } else if (guiID == ELEC_GUI_STUFF){
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof IInventoryTile){
                return ((IInventoryTile) tile).getGuiServer(entityPlayer);
            }
            return null;
        } else {
            Block block = world.getBlock(x, y, z);
            if (block instanceof GenericBlock) {
                GenericBlock genericBlock = (GenericBlock) block;
                TileEntity te = world.getTileEntity(x, y, z);
                return genericBlock.createServerContainer(entityPlayer, te);
            }
            return null;
        }
    }

    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (guiID == GUI_MANUAL_MAIN) {
            return new GuiDeepResonanceManual();
        } else if (guiID == ELEC_GUI_STUFF){
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof IInventoryTile){
                return ((IInventoryTile) tile).getGuiClient(entityPlayer);
            }
            return null;
        } else {
            Block block = world.getBlock(x, y, z);
            if (block instanceof GenericBlock) {
                GenericBlock genericBlock = (GenericBlock) block;
                TileEntity te = world.getTileEntity(x, y, z);
                return genericBlock.createClientGui(entityPlayer, te);
            }
            return null;
        }
    }
}
