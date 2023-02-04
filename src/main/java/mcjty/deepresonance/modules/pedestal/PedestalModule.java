package mcjty.deepresonance.modules.pedestal;

import mcjty.deepresonance.modules.pedestal.block.PedestalTileEntity;
import mcjty.deepresonance.modules.pedestal.client.PedestalGui;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.deepresonance.setup.Registration.*;

public class PedestalModule implements IModule {

    public static final RegistryObject<BaseBlock> PEDESTAL = BLOCKS.register("pedestal", PedestalTileEntity::createBlock);
    public static final RegistryObject<Item> PEDESTAL_ITEM = ITEMS.register("pedestal", () -> new BlockItem(PEDESTAL.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_PEDESTAL = TILES.register("pedestal", () -> BlockEntityType.Builder.of(PedestalTileEntity::new, PEDESTAL.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_PEDESTAL = CONTAINERS.register("pedestal", GenericContainer::createContainerType);

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            PedestalGui.register();
        });
    }

    @Override
    public void initConfig() {
    }
}
