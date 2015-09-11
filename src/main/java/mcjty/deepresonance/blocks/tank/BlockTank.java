package mcjty.deepresonance.blocks.tank;

import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map;

/**
 * Created by Elec332 on 20-8-2015.
 */
public class BlockTank extends ElecGenericBlockBase {

    private IIcon iconSideProvide;
    private IIcon iconSideAccept;
    private IIcon iconTopProvide;
    private IIcon iconTopAccept;
    private IIcon iconBottomProvide;
    private IIcon iconBottomAccept;

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
            world.markBlockForUpdate(x, y, z);
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, side, sidex, sidey, sidez);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public String getTopIconName() {
        return "tankTop";
    }

    @Override
    public String getBottomIconName() {
        return "tankBottom";
    }

    @Override
    public String getSideIconName() {
        return "tankSide";
    }

    public IIcon getSideIcon() {
        return iconSide;
    }

    public IIcon getBottomIcon() {
        return iconBottom;
    }

    public IIcon getTopIcon() {
        return iconTop;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        iconSideAccept = iconRegister.registerIcon(modBase.getModId() + ":tankSideAccept");
        iconSideProvide = iconRegister.registerIcon(modBase.getModId() + ":tankSideProvide");
        iconTopAccept = iconRegister.registerIcon(modBase.getModId() + ":tankTopAccept");
        iconTopProvide = iconRegister.registerIcon(modBase.getModId() + ":tankTopProvide");
        iconBottomAccept = iconRegister.registerIcon(modBase.getModId() + ":tankBottomAccept");
        iconBottomProvide = iconRegister.registerIcon(modBase.getModId() + ":tankBottomProvide");
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return world.getBlock(x, y, z) != this;
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity te = blockAccess.getTileEntity(x, y, z);
        if (te instanceof TileTank) {
            TileTank tileTank = (TileTank) te;
            ForgeDirection dir = ForgeDirection.getOrientation(side);
            if (dir == ForgeDirection.DOWN) {
                if (tileTank.isInput(dir)) {
                    return iconBottomAccept;
                } else if (tileTank.isOutput(dir)) {
                    return iconBottomProvide;
                }
                return iconBottom;
            } else if (dir == ForgeDirection.UP) {
                if (tileTank.isInput(dir)) {
                    return iconTopAccept;
                } else if (tileTank.isOutput(dir)) {
                    return iconTopProvide;
                }
                return iconTop;
            } else {
                if (tileTank.isInput(dir)) {
                    return iconSideAccept;
                } else if (tileTank.isOutput(dir)) {
                    return iconSideProvide;
                }
                return iconSide;
            }
        } else {
            return getIcon(side, 0);
        }
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
