package mcjty.deepresonance.proxy;

import mcjty.deepresonance.items.manual.GuiDeepResonanceManual;
import mcjty.lib.blocks.GenericBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

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

    @Override
    public Object getServerGuiElement(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (guiID == GUI_MANUAL_MAIN) {
            return null;
        } else {
            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof GenericBlock) {
                GenericBlock genericBlock = (GenericBlock) block;
                TileEntity te = world.getTileEntity(pos);
                return genericBlock.createServerContainer(entityPlayer, te);
            }
            return null;
        }
    }

    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (guiID == GUI_MANUAL_MAIN) {
            return new GuiDeepResonanceManual();
        }  else {
            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof GenericBlock) {
                GenericBlock genericBlock = (GenericBlock) block;
                TileEntity te = world.getTileEntity(pos);
                return genericBlock.createClientGui(entityPlayer, te);
            }
            return null;
        }
    }
}
