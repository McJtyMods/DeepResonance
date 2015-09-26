package mcjty.deepresonance.blocks.pedestal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.container.GenericBlock;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.gui.GuiProxy;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class PedestalBlock extends GenericBlock {

    public PedestalBlock() {
        super(DeepResonance.instance, Material.iron, PedestalTileEntity.class, false);
        setBlockName("pedestalBlock");
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        PedestalTileEntity pedestalTileEntity = (PedestalTileEntity) tileEntity;
        PedestalContainer pedestalContainer = new PedestalContainer(entityPlayer, pedestalTileEntity);
        return new GuiPedestal(pedestalTileEntity, pedestalContainer);
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new PedestalContainer(entityPlayer, (PedestalTileEntity) tileEntity);
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_PEDESTAL;
    }


    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("This block can place crystals and pick up spent.");
            list.add("crystals");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public String getIdentifyingIconName() {
        return "pedestal";
    }
}
