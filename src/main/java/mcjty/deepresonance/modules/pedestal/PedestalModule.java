package mcjty.deepresonance.modules.pedestal;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.pedestal.block.PedestalTileEntity;
import mcjty.deepresonance.modules.pedestal.client.PedestalGui;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.deepresonance.DeepResonance.tab;
import static mcjty.deepresonance.datagen.BlockStates.DEFAULT_BOTTOM;
import static mcjty.deepresonance.setup.Registration.*;

public class PedestalModule implements IModule {

    public static final RegistryObject<BaseBlock> PEDESTAL = BLOCKS.register("pedestal", PedestalTileEntity::createBlock);
    public static final RegistryObject<Item> PEDESTAL_ITEM = ITEMS.register("pedestal", tab(() -> new BlockItem(PEDESTAL.get(), Registration.createStandardProperties())));
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

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(PEDESTAL)
                        .standardLoot(TYPE_PEDESTAL)
                        .ironPickaxeTags()
                        .blockState(p -> p.orientedBlock(PEDESTAL.get(), p.frontBasedModel(p.name(PEDESTAL.get()), p.modLoc("block/pedestal"), DEFAULT_BOTTOM, DEFAULT_BOTTOM, DEFAULT_BOTTOM)))
                        .parentedItem()
                        .shaped(builder -> builder
                                .define('i', Tags.Items.INGOTS_IRON)
                                .define('P', Blocks.DISPENSER)
                                .define('C', Blocks.COMPARATOR)
                                .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                .pattern("iPi")
                                .pattern("imi")
                                .pattern("iCi")
                                .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())))
                        );
    }
}
