package mcjty.deepresonance.blocks.purifier;

import mcjty.deepresonance.items.ModItems;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.container.SlotType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PurifierContainer extends GenericContainer {
    public static final String CONTAINER_INVENTORY = "container";

    public static final int SLOT_FILTERINPUT = 0;

    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            addSlotBox(new SlotDefinition(SlotType.SLOT_SPECIFICITEM, new ItemStack(ModItems.filterMaterialItem)), CONTAINER_INVENTORY, SLOT_FILTERINPUT, 64, 24, 1, 18, 1, 18);
            layoutPlayerInventorySlots(10, 70);
        }
    };

    public PurifierContainer(EntityPlayer player, PurifierTileEntity containerInventory) {
        super(factory);
        addInventory(CONTAINER_INVENTORY, containerInventory);
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
    }
}
