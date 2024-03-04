package mcjty.deepresonance.modules.machines;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.block.*;
import mcjty.deepresonance.modules.machines.client.*;
import mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry;
import mcjty.deepresonance.modules.machines.item.ItemLens;
import mcjty.deepresonance.modules.machines.util.config.*;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.deepresonance.DeepResonance.tab;
import static mcjty.deepresonance.datagen.BlockStates.*;
import static mcjty.deepresonance.setup.Registration.CONTAINERS;
import static mcjty.deepresonance.setup.Registration.TILES;

public class MachinesModule implements IModule {

    public static final DeferredBlock<Block> VALVE_BLOCK = Registration.BLOCKS.register("valve", ValveTileEntity::createBlock);
    public static final DeferredItem<Item> VALVE_ITEM = Registration.fromBlock(VALVE_BLOCK);
    public static final Supplier<BlockEntityType<ValveTileEntity>> TYPE_VALVE = TILES.register("valve", () -> BlockEntityType.Builder.of(ValveTileEntity::new, VALVE_BLOCK.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> VALVE_CONTAINER = CONTAINERS.register("valve", GenericContainer::createContainerType);

    public static final DeferredBlock<Block> SMELTER_BLOCK = Registration.BLOCKS.register("smelter", SmelterTileEntity::createBlock);
    public static final DeferredItem<Item> SMELTER_ITEM = Registration.fromBlock(SMELTER_BLOCK);
    public static final Supplier<BlockEntityType<SmelterTileEntity>> TYPE_SMELTER = TILES.register("smelter", () -> BlockEntityType.Builder.of(SmelterTileEntity::new, SMELTER_BLOCK.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> SMELTER_CONTAINER = CONTAINERS.register("smelter", GenericContainer::createContainerType);

    public static final DeferredBlock<Block> PURIFIER_BLOCK = Registration.BLOCKS.register("purifier", PurifierTileEntity::createBlock);
    public static final DeferredItem<Item> PURIFIER_ITEM = Registration.fromBlock(PURIFIER_BLOCK);
    public static final Supplier<BlockEntityType<PurifierTileEntity>> TYPE_PURIFIER = TILES.register("purifier", () -> BlockEntityType.Builder.of(PurifierTileEntity::new, PURIFIER_BLOCK.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> PURIFIER_CONTAINER = CONTAINERS.register("purifier", GenericContainer::createContainerType);

    public static final DeferredBlock<LensBlock> LENS_BLOCK = Registration.BLOCKS.register("lens", LensBlock::new);
    public static final DeferredItem<Item> LENS_ITEM = Registration.ITEMS.register("lens", tab(() -> new ItemLens(LENS_BLOCK.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<LensTileEntity>> TYPE_LENS = TILES.register("lens", () -> BlockEntityType.Builder.of(LensTileEntity::new, LENS_BLOCK.get()).build(null));

    public static final DeferredBlock<Block> LASER_BLOCK = Registration.BLOCKS.register("laser", LaserTileEntity::createBlock);
    public static final DeferredItem<Item> LASER_ITEM = Registration.fromBlock(LASER_BLOCK);
    public static final Supplier<BlockEntityType<LaserTileEntity>> TYPE_LASER = TILES.register("laser", () -> BlockEntityType.Builder.of(LaserTileEntity::new, LASER_BLOCK.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> LASER_CONTAINER = CONTAINERS.register("laser", GenericContainer::createContainerType);

    public static final DeferredBlock<Block> CRYSTALLIZER_BLOCK = Registration.BLOCKS.register("crystallizer", CrystallizerTileEntity::createBlock);
    public static final DeferredItem<Item> CRYSTALLIZER_ITEM = Registration.fromBlock(CRYSTALLIZER_BLOCK);
    public static final Supplier<BlockEntityType<CrystallizerTileEntity>> TYPE_CRYSTALIZER = TILES.register("crystallizer", () -> BlockEntityType.Builder.of(CrystallizerTileEntity::new, CRYSTALLIZER_BLOCK.get()).build(null));
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
                Dob.blockBuilder(VALVE_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem()
                        .standardLoot(TYPE_VALVE)
                        .blockState(provider -> provider.simpleBlock(VALVE_BLOCK.get(), provider.models().cubeBottomTop(provider.name(VALVE_BLOCK.get()), new ResourceLocation(DeepResonance.MODID, "block/valve"), DEFAULT_BOTTOM, DEFAULT_TOP)))
                        .shaped(builder -> builder
                                        .define('F', CoreModule.FILTER_MATERIAL_ITEM.get())
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .define('C', Items.COMPARATOR)
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "GGG", "FmF", "GCG"),
                Dob.blockBuilder(SMELTER_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem()
                        .standardLoot(TYPE_SMELTER)
                        .blockState(provider -> provider.horizontalOrientedBlock(SMELTER_BLOCK.get(), (state, builder) -> {
                            if (state.getValue(BlockStateProperties.POWERED)) {
                                builder.modelFile(provider.frontBasedModel(provider.name(state.getBlock()) + "_active", new ResourceLocation(DeepResonance.MODID, "block/smelter_active"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
                            } else {
                                builder.modelFile(provider.frontBasedModel(provider.name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/smelter"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
                            }
                        }))
                        .shaped(builder -> builder
                                        .define('F', CoreModule.FILTER_MATERIAL_ITEM.get())
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "FFF", "imi", "FFF"),
                Dob.blockBuilder(PURIFIER_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem()
                        .standardLoot(TYPE_PURIFIER)
                        .blockState(provider -> provider.horizontalOrientedBlock(PURIFIER_BLOCK.get(),
                                (state, builder) -> builder.modelFile(provider.frontBasedModel(
                                        provider.name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/purifier"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM))))
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .define('x', Items.NETHER_BRICK)
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "PPP", "imi", "xxx"),
                Dob.blockBuilder(LENS_BLOCK)
                        .ironPickaxeTags()
                        .simpleLoot()
                        .parentedItem("block/lens_mc")
                        .blockState(provider -> {
                            provider.horizontalOrientedBlock(LENS_BLOCK.get(), provider.models()
                                    .withExistingParent("lens_mc", new ResourceLocation(DeepResonance.MODID, "lens"))
                                    .texture("lens_texture", "deepresonance:block/lens")
                                    .texture("particle", "deepresonance:block/lens"));

                        })
                        .shaped(builder -> builder
                                        .define('g', Tags.Items.GLASS_PANES)
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_pane", DataGen.has(Tags.Items.GLASS_PANES)),
                                "gPg", "P P", "gPg"),
                Dob.blockBuilder(LASER_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem()
                        .standardLoot(TYPE_LASER)
                        .blockState(provider -> provider.horizontalOrientedBlock(LASER_BLOCK.get(), DataGenHelper.createLaserModel(provider)))
                        .shaped(builder -> builder
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .define('X', Tags.Items.INGOTS_GOLD)
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "GXG", "eme", "ddd"),
                Dob.blockBuilder(CRYSTALLIZER_BLOCK)
                        .ironPickaxeTags()
                        .parentedItem()
                        .standardLoot(TYPE_CRYSTALIZER)
                        .blockState(provider -> provider.horizontalBlock(CRYSTALLIZER_BLOCK.get(), DataGenHelper.createCrystallizerModel(provider)))
                        .shaped(builder -> builder
                                        .define('q', Items.QUARTZ)
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .define('X', Tags.Items.INGOTS_GOLD)
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "GXG", "qmq", "iii")
        );
    }
}
