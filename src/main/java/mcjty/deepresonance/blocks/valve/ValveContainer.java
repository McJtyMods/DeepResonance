package mcjty.deepresonance.blocks.valve;

import mcjty.container.ContainerFactory;
import mcjty.container.GenericContainer;
import mcjty.container.SlotDefinition;
import mcjty.container.SlotType;
import mcjty.deepresonance.blocks.purifier.PurifierTileEntity;
import mcjty.deepresonance.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ValveContainer extends GenericContainer {
    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            layoutPlayerInventorySlots(10, 70);
        }
    };

    public ValveContainer(EntityPlayer player) {
        super(factory);
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
    }
}
