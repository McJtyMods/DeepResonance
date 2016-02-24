package mcjty.deepresonance.blocks.laser;

import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.lib.container.GenericGuiContainer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

//@Optional.InterfaceList({
      //  @Optional.Interface(iface = "crazypants.enderio.api.redstone.IRedstoneConnectable", modid = "EnderIO")})
public class LaserBlock extends GenericDRBlock<LaserTileEntity, LaserContainer> {

    public static PropertyInteger COLOR = PropertyInteger.create("color", 0, 3);

    public LaserBlock() {
        super(Material.iron, LaserTileEntity.class, LaserContainer.class, "laser", false);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GuiLaser.class;
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_LASER;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        super.initModel();
        ClientRegistry.bindTileEntitySpecialRenderer(LaserTileEntity.class, new LaserRenderer());
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("Place this laser so it faces a lens.");
            list.add("It will infuse the liquid in the tank");
            list.add("depending on the materials used.");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        boolean rc = super.rotateBlock(world, pos, axis);
        if (world.isRemote) {
            // Make sure rendering is up to date.
            world.markBlockForUpdate(pos);
        }
        return rc;
    }

    /*
    @Override
    public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from) {
        return true;
    }
    */

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        checkRedstoneWithTE(world, pos);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(FACING_HORIZ).getIndex() - 2) + ((state.getValue(COLOR)) << 2);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING_HORIZ, getFacingHoriz(meta & 3)).withProperty(COLOR, (meta >> 2));
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING_HORIZ, COLOR);
    }
}
