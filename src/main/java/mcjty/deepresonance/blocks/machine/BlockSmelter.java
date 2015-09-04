package mcjty.deepresonance.blocks.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
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

/**
 * Created by Elec332 on 20-8-2015.
 */
public class BlockSmelter extends ElecGenericBlockBase {

    public BlockSmelter(String blockName) {
        super(Material.rock, TileSmelter.class, blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        currentTip.add("TESTERT");
        return currentTip;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        TileSmelter smelterTile = (TileSmelter) tileEntity;
        SmelterContainer smelterContainer = new SmelterContainer(entityPlayer, smelterTile);
        return new GuiSmelter(smelterTile, smelterContainer);
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new SmelterContainer(entityPlayer, (TileSmelter) tileEntity);
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_SMELTER;
    }

}
