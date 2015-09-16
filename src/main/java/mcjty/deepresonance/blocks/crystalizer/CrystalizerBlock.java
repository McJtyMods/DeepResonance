package mcjty.deepresonance.blocks.crystalizer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
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
import net.minecraft.util.IIcon;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class CrystalizerBlock extends ElecGenericBlockBase {

    public CrystalizerBlock(String blockName) {
        super(Material.rock, CrystalizerTileEntity.class, blockName);
        setCreativeTab(DeepResonance.tabDeepResonance);
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
            list.add("This machine will crystalize the liquid crystal");
            list.add("from the tank below it and eventually produce a crystal");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public String getIdentifyingIconName() {
        return "crystalizer";
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_CRYSTALIZER;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    public IIcon getTopIcon() {
        return iconTop;
    }

    public IIcon getBottomIcon() {
        return iconBottom;
    }

    public IIcon getSideIcon() {
        return iconSide;
    }

    public IIcon getSouthIcon() {
        return iconInd;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        CrystalizerTileEntity crystalizerTileEntity = (CrystalizerTileEntity) tileEntity;
        CrystalizerContainer crystalizerContainer = new CrystalizerContainer(entityPlayer, crystalizerTileEntity);
        return new GuiCrystalizer(crystalizerTileEntity, crystalizerContainer);
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new CrystalizerContainer(entityPlayer, (CrystalizerTileEntity) tileEntity);
    }


}
