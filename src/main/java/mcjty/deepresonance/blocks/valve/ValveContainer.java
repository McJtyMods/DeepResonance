package mcjty.deepresonance.blocks.valve;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import net.minecraft.entity.player.EntityPlayer;

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
