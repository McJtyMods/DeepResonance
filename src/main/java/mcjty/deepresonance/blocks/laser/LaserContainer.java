package mcjty.deepresonance.blocks.laser;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.container.SlotType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class LaserContainer extends GenericContainer {
    public static final String CONTAINER_INVENTORY = "container";

    public static final int SLOT_CRYSTAL = 0;
    public static final int SLOT_CATALYST = 1;

    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            addSlotBox(new SlotDefinition(SlotType.SLOT_SPECIFICITEM, new ItemStack(ModBlocks.resonatingCrystalBlock)), CONTAINER_INVENTORY, SLOT_CRYSTAL, 154, 48, 1, 18, 1, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_INPUT), CONTAINER_INVENTORY, SLOT_CATALYST, 20, 7, 1, 18, 1, 18);
            layoutPlayerInventorySlots(10, 70);
        }
    };

    public LaserContainer(EntityPlayer player, LaserTileEntity containerInventory) {
        super(factory);
        addInventory(CONTAINER_INVENTORY, containerInventory);
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
    }
}
