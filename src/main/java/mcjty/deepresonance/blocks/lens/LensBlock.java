package mcjty.deepresonance.blocks.lens;

import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.List;

public class LensBlock extends GenericDRBlock<LensTileEntity, EmptyContainer> {

    public LensBlock() {
        super(Material.IRON, LensTileEntity.class, EmptyContainer::new, LensItemBlock.class, "lens", false);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("Place this block on a tank and aim an");
            list.add("infusion laser at it to enhance the liquid");
            list.add("in the tank");
        } else {
            list.add(TextFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    // @todo @@@@@ check IBlockAccess vs World
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }
//    @Override
//    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
//        return null;
//    }

    public static final AxisAlignedBB BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    public static final AxisAlignedBB NORTH_BLOCK_AABB = new AxisAlignedBB(0.1F, 0.1F, 0.0F, 0.9F, 0.9F, 0.1F);
    public static final AxisAlignedBB SOUTH_BLOCK_AABB = new AxisAlignedBB(0.1F, 0.1F, 0.9F, 0.9F, 0.9F, 1.0F);
    public static final AxisAlignedBB WEST_BLOCK_AABB = new AxisAlignedBB(0.0F, 0.1F, 0.1F, 0.1F, 0.9F, 0.9F);
    public static final AxisAlignedBB EAST_BLOCK_AABB = new AxisAlignedBB(0.9F, 0.1F, 0.1F, 1.0F, 0.9F, 0.9F);


    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing direction = OrientationTools.getOrientationHoriz(state);
        switch (direction) {
            case NORTH:
                return NORTH_BLOCK_AABB;
            case SOUTH:
                return SOUTH_BLOCK_AABB;
            case WEST:
                return WEST_BLOCK_AABB;
            case EAST:
                return EAST_BLOCK_AABB;
            case DOWN:
            case UP:
            default:
                return BLOCK_AABB;
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
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

}
