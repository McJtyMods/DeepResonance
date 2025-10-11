package mcjty.deepresonance.modules.pedestal;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.pedestal.block.PedestalTileEntity;
import mcjty.deepresonance.modules.pedestal.client.PedestalGui;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

import static mcjty.deepresonance.datagen.BlockStates.DEFAULT_BOTTOM;
import static mcjty.deepresonance.setup.Registration.CONTAINERS;

public class PedestalModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, PedestalTileEntity> PEDESTAL = Registration.registerBlockWithTile(
            "pedestal",
            PedestalTileEntity.class,
            PedestalTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            PedestalTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PedestalTileEntity>> TYPE_PEDESTAL = PEDESTAL.be();
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_PEDESTAL = CONTAINERS.register("pedestal", GenericContainer::createContainerType);

    public PedestalModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        PedestalGui.register(event);
    }

    @Override
    public void initConfig(IEventBus bus) {
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider registries) {
        dataGen.add(
                Dob.blockBuilder(PEDESTAL)
//                        .standardLoot(TYPE_PEDESTAL)  // @todo 1.21
                        .ironPickaxeTags()
                        .blockState(p -> p.orientedBlock(PEDESTAL.block().get(), p.frontBasedModel(p.name(PEDESTAL.block().get()), p.modLoc("block/pedestal"), DEFAULT_BOTTOM, DEFAULT_BOTTOM, DEFAULT_BOTTOM)))
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
