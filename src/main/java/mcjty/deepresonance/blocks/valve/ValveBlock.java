package mcjty.deepresonance.blocks.valve;

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

@SuppressWarnings("unchecked")
public class ValveBlock extends GenericDRBlock {

    public ValveBlock(String blockName) {
        super(Material.rock, ValveTileEntity.class, null, blockName, true);
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
            list.add("This machine will transfer fluids from the upper tank");
            list.add("to a tank below if the fluid matches certain conditions");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        ValveTileEntity valveTileEntity = (ValveTileEntity) tileEntity;
        ValveContainer valveContainer = new ValveContainer(entityPlayer);
        return new GuiValve(valveTileEntity, valveContainer);
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new ValveContainer(entityPlayer);
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_VALVE;
    }

}
