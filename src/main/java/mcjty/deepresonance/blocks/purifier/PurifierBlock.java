package mcjty.deepresonance.blocks.purifier;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
import mcjty.deepresonance.gui.GuiProxy;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public class PurifierBlock extends ElecGenericBlockBase {

    public PurifierBlock(String blockName) {
        super(Material.rock, PurifierTileEntity.class, blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
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

    @Override
    public String getSideIconName() {
        return "purifier";
    }
}
