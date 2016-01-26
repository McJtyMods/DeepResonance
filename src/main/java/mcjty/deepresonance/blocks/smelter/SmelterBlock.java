package mcjty.deepresonance.blocks.smelter;

import mcjty.deepresonance.blocks.GenericDRBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.lib.varia.BlockTools;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.input.Keyboard;

import java.util.List;

/**
 * Created by Elec332 on 20-8-2015.
 */
public class SmelterBlock extends GenericDRBlock {

    public SmelterBlock(String blockName) {
        super(Material.rock, SmelterTileEntity.class, null, blockName, true);
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

    @Override //TODO: McJty: This isn't needed anymore, cuz JSON
    public String getIdentifyingIconName() {
        return "smelterActive";
    }

}
