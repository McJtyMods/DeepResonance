package mcjty.deepresonance.modules.machines;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.block.*;
import mcjty.deepresonance.modules.machines.client.*;
import mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry;
import mcjty.deepresonance.modules.machines.item.ItemLens;
import mcjty.deepresonance.modules.machines.util.config.*;
import mcjty.lib.blocks.BaseBlock;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

import static mcjty.deepresonance.datagen.BlockStates.*;
import static mcjty.deepresonance.setup.Registration.CONTAINERS;

public class MachinesModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, ValveTileEntity> VALVE = Registration.registerBlockWithTile(
            "valve",
            ValveTileEntity.class,
            ValveTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            ValveTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ValveTileEntity>> TYPE_VALVE = VALVE.be();
    public static final Supplier<MenuType<GenericContainer>> VALVE_CONTAINER = CONTAINERS.register("valve", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, SmelterTileEntity> SMELTER = Registration.registerBlockWithTile(
            "smelter",
            SmelterTileEntity.class,
            SmelterTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            SmelterTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmelterTileEntity>> TYPE_SMELTER = SMELTER.be();
    public static final Supplier<MenuType<GenericContainer>> SMELTER_CONTAINER = CONTAINERS.register("smelter", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, PurifierTileEntity> PURIFIER = Registration.registerBlockWithTile(
            "purifier",
            PurifierTileEntity.class,
            PurifierTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            PurifierTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PurifierTileEntity>> TYPE_PURIFIER = PURIFIER.be();
    public static final Supplier<MenuType<GenericContainer>> PURIFIER_CONTAINER = CONTAINERS.register("purifier", GenericContainer::createContainerType);

    public static final RBlock<LensBlock, ItemLens, LensTileEntity> LENS = Registration.registerBlockWithTile(
            "lens",
            LensTileEntity.class,
            LensBlock::new,
            block -> new ItemLens((LensBlock) block.get(), Registration.createStandardProperties()),
            LensTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LensTileEntity>> TYPE_LENS = LENS.be();

    public static final RBlock<BaseBlock, BlockItem, LaserTileEntity> LASER = Registration.registerBlockWithTile(
            "laser",
            LaserTileEntity.class,
            LaserTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            LaserTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LaserTileEntity>> TYPE_LASER = LASER.be();
    public static final Supplier<MenuType<GenericContainer>> LASER_CONTAINER = CONTAINERS.register("laser", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, CrystallizerTileEntity> CRYSTALLIZER = Registration.registerBlockWithTile(
            "crystallizer",
            CrystallizerTileEntity.class,
            CrystallizerTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            CrystallizerTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystallizerTileEntity>> TYPE_CRYSTALIZER = CRYSTALLIZER.be();
    public static final Supplier<MenuType<GenericContainer>> CRYSTALIZER_CONTAINER = CONTAINERS.register("crystallizer", GenericContainer::createContainerType);

    public MachinesModule() {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        CrystallizerRenderer.register();
        LaserRenderer.register();

        event.enqueueWork(() -> {
            SmelterGui.register();
            PurifierGui.register();
            LaserGui.register();
            ValveGui.register();
            CrystallizerGui.register();
        });
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        InfusionBonusRegistry.createDefaultInfusionBonusMap();
    }

    @Override
    public void initConfig(IEventBus bus) {
        CrystallizerConfig.init();
        LaserConfig.init();
        PurifierConfig.init();
        SmelterConfig.init();
        ValveConfig.init();
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(VALVE)
                        .ironPickaxeTags()
                        .parentedItem()
                        .standardLoot(TYPE_VALVE)
                        .blockState(provider -> provider.simpleBlock(VALVE.block().get(), provider.models().cubeBottomTop(provider.name(VALVE.block().get()), ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "block/valve"), DEFAULT_BOTTOM, DEFAULT_TOP)))
                        .shaped(builder -> builder
                                        .define('F', CoreModule.FILTER_MATERIAL_ITEM.get())
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .define('C', Items.COMPARATOR)
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "GGG", "FmF", "GCG"),
                Dob.blockBuilder(SMELTER)
                        .ironPickaxeTags()
                        .parentedItem()
                        .standardLoot(TYPE_SMELTER)
                        .blockState(provider -> provider.horizontalOrientedBlock(SMELTER.block().get(), (state, builder) -> {
                            if (state.getValue(BlockStateProperties.POWERED)) {
                                builder.modelFile(provider.frontBasedModel(provider.name(state.getBlock()) + "_active", ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "block/smelter_active"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
                            } else {
                                builder.modelFile(provider.frontBasedModel(provider.name(state.getBlock()), ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "block/smelter"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
                            }
                        }))
                        .shaped(builder -> builder
                                        .define('F', CoreModule.FILTER_MATERIAL_ITEM.get())
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "FFF", "imi", "FFF"),
                Dob.blockBuilder(PURIFIER)
                        .ironPickaxeTags()
                        .parentedItem()
                        .standardLoot(TYPE_PURIFIER)
                        .blockState(provider -> provider.horizontalOrientedBlock(PURIFIER.block().get(),
                                (state, builder) -> builder.modelFile(provider.frontBasedModel(
                                        provider.name(state.getBlock()), ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "block/purifier"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM))))
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .define('x', Items.NETHER_BRICK)
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "PPP", "imi", "xxx"),
                Dob.blockBuilder(LENS)
                        .ironPickaxeTags()
                        .simpleLoot()
                        .parentedItem("block/lens_mc")
                        .blockState(provider -> {
                            provider.horizontalOrientedBlock(LENS.block().get(), provider.models()
                                    .withExistingParent("lens_mc", ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "lens"))
                                    .texture("lens_texture", "deepresonance:block/lens")
                                    .texture("particle", "deepresonance:block/lens"));

                        })
                        .shaped(builder -> builder
                                        .define('g', Tags.Items.GLASS_PANES)
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_pane", DataGen.has(Tags.Items.GLASS_PANES)),
                                "gPg", "P P", "gPg"),
                Dob.blockBuilder(LASER)
                        .ironPickaxeTags()
                        .parentedItem()
                        .standardLoot(TYPE_LASER)
                        .blockState(provider -> provider.horizontalOrientedBlock(LASER.block().get(), DataGenHelper.createLaserModel(provider)))
                        .shaped(builder -> builder
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .define('X', Tags.Items.INGOTS_GOLD)
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "GXG", "eme", "ddd"),
                Dob.blockBuilder(CRYSTALLIZER)
                        .ironPickaxeTags()
                        .parentedItem()
                        .standardLoot(TYPE_CRYSTALIZER)
                        .blockState(provider -> provider.horizontalBlock(CRYSTALLIZER.block().get(), DataGenHelper.createCrystallizerModel(provider)))
                        .shaped(builder -> builder
                                        .define('q', Items.QUARTZ)
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .define('X', Tags.Items.INGOTS_GOLD)
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "GXG", "qmq", "iii")
        );
    }
}
