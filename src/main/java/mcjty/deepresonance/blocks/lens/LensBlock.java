package mcjty.deepresonance.blocks.lens;

import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.varia.BlockTools;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class LensBlock extends GenericDRBlock<LensTileEntity, EmptyContainer> {

    public LensBlock() {
        super(Material.iron, LensTileEntity.class, EmptyContainer.class, LensItemBlock.class, "lens", false);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("Place this block on a tank and aim an");
            list.add("infusion laser at it to enhance the liquid");
            list.add("in the tank");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    @SuppressWarnings("all")
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return null;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        int meta = state.getBlock().getMetaFromState(state);
        EnumFacing direction = BlockTools.getOrientationHoriz(meta);
        switch (direction) {
            case DOWN:
            case UP:
                break;
            case NORTH:
                this.setBlockBounds(0.1F, 0.1F, 0.0F, 0.9F, 0.9F, 0.1F);
                break;
            case SOUTH:
                this.setBlockBounds(0.1F, 0.1F, 0.9F, 0.9F, 0.9F, 1.0F);
                break;
            case WEST:
                this.setBlockBounds(0.0F, 0.1F, 0.1F, 0.1F, 0.9F, 0.9F);
                break;
            case EAST:
                this.setBlockBounds(0.9F, 0.1F, 0.1F, 1.0F, 0.9F, 0.9F);
                break;
        }
    }

    /*
    @Override
    protected boolean wrenchUse(World world, int x, int y, int z, EntityPlayer player) {
        // Do not rotate
        return true;
    }*/

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }


    @Override
    public boolean isFullBlock() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

}
