package mcjty.deepresonance.modules.generator;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.block.*;
import mcjty.deepresonance.modules.generator.client.ClientSetup;
import mcjty.deepresonance.modules.generator.client.CollectorRenderer;
import mcjty.deepresonance.modules.generator.util.CollectorConfig;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.varia.SoundTools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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

public class GeneratorModule implements IModule {

    public static final RBlock<EnergyCollectorBlock, BlockItem, EnergyCollectorTileEntity> ENERGY_COLLECTOR = Registration.registerBlockWithTile(
            "energy_collector",
            EnergyCollectorTileEntity.class,
            EnergyCollectorBlock::new,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            EnergyCollectorTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyCollectorTileEntity>> TYPE_ENERGY_COLLECTOR = ENERGY_COLLECTOR.be();

    public static final RBlock<GeneratorControllerBlock, BlockItem, GeneratorControllerTileEntity> GENERATOR_CONTROLLER = Registration.registerBlockWithTile(
            "generator_controller",
            GeneratorControllerTileEntity.class,
            GeneratorControllerBlock::new,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            GeneratorControllerTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneratorControllerTileEntity>> TYPE_GENERATOR_CONTROLLER = GENERATOR_CONTROLLER.be();

    public static final RBlock<GeneratorPartBlock, BlockItem, GeneratorPartTileEntity> GENERATOR_PART = Registration.registerBlockWithTile(
            "generator_part",
            GeneratorPartTileEntity.class,
            GeneratorPartBlock::new,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            GeneratorPartTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneratorPartTileEntity>> TYPE_GENERATOR_PART = GENERATOR_PART.be();

    public static final Supplier<SoundEvent> STARTUP_SOUND = Registration.SOUNDS.register("engine_start", () -> SoundTools.createSoundEvent(ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "engine_start")));
    public static final Supplier<SoundEvent> LOOP_SOUND = Registration.SOUNDS.register("engine_loop", () -> SoundTools.createSoundEvent(ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "engine_loop")));
    public static final Supplier<SoundEvent> SHUTDOWN_SOUND = Registration.SOUNDS.register("engine_shutdown", () -> SoundTools.createSoundEvent(ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "engine_shutdown")));

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        ClientSetup.initClient();
        CollectorRenderer.register();
    }

    @Override
    public void initConfig(IEventBus bus) {
        CollectorConfig.init();
        GeneratorConfig.init();
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(GENERATOR_PART)
                        .blockState(provider -> DataGenHelper.registerGeneratorPart(GENERATOR_PART.block(), provider))
                        .parentedItem()
                        .standardLoot(TYPE_GENERATOR_PART)
                        .ironPickaxeTags()
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .define('X', Tags.Items.INGOTS_GOLD)
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "XRX", "imi", "PRP"),
                Dob.blockBuilder(ENERGY_COLLECTOR)
                        .blockState(provider -> provider.simpleBlock(ENERGY_COLLECTOR.block().get(), provider.models().withExistingParent("energy_collector", ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "collector")).texture("collector_texture", "deepresonance:block/energy_collector")
                                .texture("particle", "deepresonance:block/energy_collector")))
                        .parentedItem()
                        .simpleLoot()
                        .ironPickaxeTags()
                        .shaped(builder -> builder
                                        .define('X', Tags.Items.INGOTS_GOLD)
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .define('q', Items.QUARTZ)
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "PdP", "qmq", "XXX"),
                Dob.blockBuilder(GENERATOR_CONTROLLER)
                        .blockState(provider -> {
                            provider.horizontalOrientedBlock(GeneratorModule.GENERATOR_CONTROLLER.block().get(), (state, builder) -> {
                                if (state.getValue(BlockStateProperties.POWERED)) {
                                    builder.modelFile(provider.frontBasedModel(provider.name(state.getBlock()), ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "block/generator_controller_on"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
                                } else {
                                    builder.modelFile(provider.frontBasedModel(provider.name(state.getBlock()), ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "block/generator_controller"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
                                }
                            });
                        })
                        .parentedItem()
                        .simpleLoot()
                        .ironPickaxeTags()
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .define('C', Items.COMPARATOR)
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "RCR", "imi", "PiP")

        );
    }
}
