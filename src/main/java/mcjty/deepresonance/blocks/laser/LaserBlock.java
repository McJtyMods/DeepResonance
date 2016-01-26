package mcjty.deepresonance.blocks.laser;

import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.lib.container.GenericBlock;
import mcjty.lib.varia.BlockTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.util.List;

//@Optional.InterfaceList({
      //  @Optional.Interface(iface = "crazypants.enderio.api.redstone.IRedstoneConnectable", modid = "EnderIO")})
public class LaserBlock extends GenericBlock{

    public LaserBlock() {
        super(DeepResonance.instance, Material.iron, LaserTileEntity.class, false);
        setUnlocalizedName(DeepResonance.MODID + ".laserBlock");
        //setHorizRotation(true); TODO: McJty: HorizRotation
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_LASER;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        LaserTileEntity laserTileEntity = (LaserTileEntity) tileEntity;
        LaserContainer laserContainer = new LaserContainer(entityPlayer, laserTileEntity);
        return new GuiLaser(laserTileEntity, laserContainer);
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new LaserContainer(entityPlayer, (LaserTileEntity) tileEntity);
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

    @Override
    public String getIdentifyingIconName() {
        return "laserBlockFront";
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
