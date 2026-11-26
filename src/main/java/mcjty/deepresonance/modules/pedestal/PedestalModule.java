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
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import mcjty.lib.setup.DeferredHolder;

import java.util.function.Supplier;

import static mcjty.deepresonance.datagen.BlockStates.DEFAULT_BOTTOM;
import static mcjty.deepresonance.setup.Registration.CONTAINERS;

public class PedestalModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, PedestalTileEntity> PEDESTAL = Registration.registerBlockWithTile(
            "pedestal",
            PedestalTileEntity.class,
            PedestalTileEntity::createBlock,
            (block, properties) -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            PedestalTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PedestalTileEntity>> TYPE_PEDESTAL = PEDESTAL.be();
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_PEDESTAL = CONTAINERS.register("pedestal", GenericContainer::createContainerType);

    @Override
    public void init() {
    }

    @Override
    public void initClient() {
        registerMenuScreens();
    }

    public void registerMenuScreens() {
        PedestalGui.register();
    }

    @Override
    public void initConfig() {
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider registries) {
        dataGen.add(
                Dob.blockBuilder(PEDESTAL)
                        .standardLoot()
                        .ironPickaxeTags()
                        .blockState(p -> p.orientedBlock(PEDESTAL.block().get(), p.frontBasedModel(p.name(PEDESTAL.block().get()), p.modLoc("block/pedestal"), DEFAULT_BOTTOM, DEFAULT_BOTTOM, DEFAULT_BOTTOM)))
                        .parentedItem()
                        .shaped(builder -> builder
                                .define('i', ConventionalItemTags.IRON_INGOTS)
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
