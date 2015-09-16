package mcjty.deepresonance.blocks.smelter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.varia.BlockTools;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.input.Keyboard;

import java.util.List;

/**
 * Created by Elec332 on 20-8-2015.
 */
public class SmelterBlock extends ElecGenericBlockBase {

    private IIcon iconActive;
    private IIcon iconInactive;

    public SmelterBlock(String blockName) {
        super(Material.rock, SmelterTileEntity.class, blockName);
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
            list.add("This machine smelts resonating ore and produces liquid");
            list.add("crystal in a tank placed on top of this.");
            list.add("Below the smelter place a tank about half-filled with lava");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        SmelterTileEntity smelterTile = (SmelterTileEntity) tileEntity;
        SmelterContainer smelterContainer = new SmelterContainer(entityPlayer, smelterTile);
        return new GuiSmelter(smelterTile, smelterContainer);
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new SmelterContainer(entityPlayer, (SmelterTileEntity) tileEntity);
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_SMELTER;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        iconActive = iconRegister.registerIcon(DeepResonance.MODID + ":smelterActive");
        iconInactive = iconRegister.registerIcon(DeepResonance.MODID + ":smelterInactive");
    }

    @Override
    public String getIdentifyingIconName() {
        return "smelterActive";
    }

    @Override
    public IIcon getIconInd(IBlockAccess blockAccess, int x, int y, int z, int meta) {
        if (BlockTools.getRedstoneSignalIn(meta)) {
            return iconActive;
        } else {
            return iconInactive;
        }
    }

}
