package mcjty.deepresonance.blocks.base;

import elec332.core.baseclasses.tileentity.IInventoryTile;
import elec332.core.baseclasses.tileentity.TileBase;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.gui.GuiProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * Created by Elec332 on 12-8-2015.
 */
public abstract class DRTileBase extends TileBase implements IInventoryTile {

    @Override
    public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return openGui(player, DeepResonance.instance, GuiProxy.ELEC_GUI_STUFF);
    }

    @Override
    public Container getGuiServer(EntityPlayer entityPlayer) {
        return (Container) getGui(entityPlayer, false);
    }

    @Override
    public Object getGuiClient(EntityPlayer entityPlayer) {
        return getGui(entityPlayer, true);
    }

    public abstract Object getGui(EntityPlayer player, boolean client);
}
