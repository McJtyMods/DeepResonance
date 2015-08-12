package mcjty.deepresonance.inventory;

import elec332.core.inventory.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created by Elec332 on 12-8-2015.
 */
public class ContainerSmelter extends BaseContainer {

    public ContainerSmelter(EntityPlayer player, IInventory inventory) {
        super(player);
        this.addSlotToContainer(new Slot(inventory, 0, 66, 53));
        addPlayerInventoryToContainer();
    }

}
