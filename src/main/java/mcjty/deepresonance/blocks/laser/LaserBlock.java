package mcjty.deepresonance.blocks.laser;

import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.gui.GuiProxy;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

//@Optional.InterfaceList({
      //  @Optional.Interface(iface = "crazypants.enderio.api.redstone.IRedstoneConnectable", modid = "EnderIO")})
public class LaserBlock extends GenericDRBlock<LaserTileEntity, LaserContainer> {

    public LaserBlock() {
        super(Material.iron, LaserTileEntity.class, LaserContainer.class, "laser", false);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_LASER;
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("Place this laser so it faces a lens.");
            list.add("It will infuse the liquid in the tank");
            list.add("depending on the materials used.");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

   /*
    //TODO: McJty: Rotation
    @Override
    protected void rotateBlock(World world, int x, int y, int z) {
        super.rotateBlock(world, x, y, z);
        if (world.isRemote) {
            // Make sure rendering is up to date.
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from) {
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        checkRedstoneWithTE(world, x, y, z);
    }*/

}
