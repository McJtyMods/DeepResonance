package mcjty.deepresonance.blocks.purifier;

import mcjty.deepresonance.blocks.GenericDRBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.gui.GuiProxy;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class PurifierBlock extends GenericDRBlock {

    public PurifierBlock(String blockName) {
        super(Material.rock, PurifierTileEntity.class, null, blockName, true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("This machine needs filter material and will purify");
            list.add("the liquid crystal from the top tank and place it in");
            list.add("another tank below this block.");
            list.add("The spent filter will be ejected in a nearby inventory.");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        PurifierTileEntity purifierTileEntity = (PurifierTileEntity) tileEntity;
        PurifierContainer purifierContainer = new PurifierContainer(entityPlayer, purifierTileEntity);
        return new GuiPurifier(purifierTileEntity, purifierContainer);
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new PurifierContainer(entityPlayer, (PurifierTileEntity) tileEntity);
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_PURIFIER;
    }

}
