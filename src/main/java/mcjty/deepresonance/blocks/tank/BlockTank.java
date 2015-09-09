package mcjty.deepresonance.blocks.tank;

import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

import java.util.Map;

/**
 * Created by Elec332 on 20-8-2015.
 */
public class BlockTank extends ElecGenericBlockBase {

    public BlockTank(String name) {
        super(Material.rock, TileTank.class, name);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileTank) {
            TileTank tank = (TileTank) tile;
            for (Map.Entry<ITankHook, ForgeDirection> entry : tank.getConnectedHooks().entrySet()) {
                entry.getKey().hook(tank, entry.getValue());
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float sidex, float sidey, float sidez) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileTank){
            TileTank tank = (TileTank)tile;
            ForgeDirection direction = ForgeDirection.getOrientation(side);
            int i = tank.settings.get(direction);
            if (i < TileTank.SETTING_MAX) {
                i++;
            } else {
                i = TileTank.SETTING_NONE;
            }
            tank.settings.put(direction, i);
            tank.markDirty();
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, side, sidex, sidey, sidez);
    }

    @Override
    public String getSideIconName() {
        return "tankSide";
    }

//    @Override
//    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
//        return false;
//    }
//
    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return getIcon(side, 0);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        if (dir == ForgeDirection.DOWN) {
            return iconBottom;
        } else if (dir == ForgeDirection.UP) {
            return iconTop;
        } else {
            return iconSide;
        }
    }
}
