package mcjty.deepresonance.blocks.gencontroller;

import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

//@Optional.InterfaceList({
       // @Optional.Interface(iface = "crazypants.enderio.api.redstone.IRedstoneConnectable", modid = "EnderIO")})
public class GeneratorControllerBlock extends GenericDRBlock<GeneratorControllerTileEntity, EmptyContainer> {

    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public GeneratorControllerBlock() {
        super(Material.iron, GeneratorControllerTileEntity.class, EmptyContainer.class, "generator_controller", false);
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
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("Part of a generator multi-block.");
            list.add("Use this block to turn on/off the reactor with");
            list.add("a redstone signal.");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
        checkRedstoneWithTE(world, pos);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        boolean working = false;
        if (te instanceof GeneratorControllerTileEntity) {
            working = ((GeneratorControllerTileEntity)te).isPowered();
        }
        return state.withProperty(POWERED, working);
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING_HORIZ, POWERED);
    }
}
