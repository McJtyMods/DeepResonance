package mcjty.deepresonance.client;

import mcjty.deepresonance.util.AbstractTileEntity;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import net.minecraft.entity.player.PlayerInventory;

/**
 * Created by Elec332 on 27-7-2020
 */
public abstract class AbstractDeepResonanceGui<T extends AbstractTileEntity> extends GenericGuiContainer<T, GenericContainer> {

    public AbstractDeepResonanceGui(T tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, ManualEntry.EMPTY);
    }

}
