package mcjty.deepresonance.modules.generator;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import static mcjty.deepresonance.DeepResonance.MODID;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.block.*;
import mcjty.deepresonance.modules.generator.client.CollectorRenderer;
import mcjty.deepresonance.modules.generator.data.GeneratorPartData;
import mcjty.deepresonance.modules.generator.util.CollectorConfig;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.varia.SoundTools;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import mcjty.lib.setup.DeferredHolder;
import net.minecraft.tags.BlockTags;

import java.util.List;
import java.util.function.Supplier;

import static mcjty.deepresonance.datagen.BlockStates.*;

public class GeneratorModule implements IModule {

    public static final RBlock<EnergyCollectorBlock, BlockItem, EnergyCollectorTileEntity> ENERGY_COLLECTOR = Registration.registerBlockWithTile(
            "energy_collector",
            EnergyCollectorTileEntity.class,
            EnergyCollectorBlock::new,
            (block, properties) -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            EnergyCollectorTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyCollectorTileEntity>> TYPE_ENERGY_COLLECTOR = ENERGY_COLLECTOR.be();

    public static final RBlock<GeneratorControllerBlock, BlockItem, GeneratorControllerTileEntity> GENERATOR_CONTROLLER = Registration.registerBlockWithTile(
            "generator_controller",
            GeneratorControllerTileEntity.class,
            GeneratorControllerBlock::new,
            (block, properties) -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            GeneratorControllerTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneratorControllerTileEntity>> TYPE_GENERATOR_CONTROLLER = GENERATOR_CONTROLLER.be();

    public static final RBlock<GeneratorPartBlock, BlockItem, GeneratorPartTileEntity> GENERATOR_PART = Registration.registerBlockWithTile(
            "generator_part",
            GeneratorPartTileEntity.class,
            GeneratorPartBlock::new,
            (block, properties) -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            GeneratorPartTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneratorPartTileEntity>> TYPE_GENERATOR_PART = GENERATOR_PART.be();

    public static final AttachmentType<GeneratorPartData> GENERATOR_PART_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(MODID, "generator_part"),
            builder -> builder.initializer(() -> GeneratorPartData.DEFAULT).persistent(GeneratorPartData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GeneratorPartData>> ITEM_GENERATOR_PART_DATA = Registration.COMPONENTS.registerComponentType(
            "generator_part",
            builder -> builder
                    .persistent(GeneratorPartData.CODEC)
                    .networkSynchronized(GeneratorPartData.STREAM_CODEC));

    public static final Supplier<SoundEvent> STARTUP_SOUND = Registration.SOUNDS.register("engine_start", () -> SoundTools.createSoundEvent(Identifier.fromNamespaceAndPath(DeepResonance.MODID, "engine_start")));
    public static final Supplier<SoundEvent> LOOP_SOUND = Registration.SOUNDS.register("engine_loop", () -> SoundTools.createSoundEvent(Identifier.fromNamespaceAndPath(DeepResonance.MODID, "engine_loop")));
    public static final Supplier<SoundEvent> SHUTDOWN_SOUND = Registration.SOUNDS.register("engine_shutdown", () -> SoundTools.createSoundEvent(Identifier.fromNamespaceAndPath(DeepResonance.MODID, "engine_shutdown")));

    @Override
    public void init() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (state.is(GENERATOR_PART.block().get()) && player instanceof ServerPlayer serverPlayer
                    && serverPlayer.connection != null) {
                // Confirm the removal before the prediction is acknowledged, even if chunk updates are deferred.
                serverPlayer.connection.send(new ClientboundBlockUpdatePacket(world, pos));
            }
        });
    }

    @Override
    public void initClient() {
        CollectorRenderer.register();
    }

    @Override
    public void initConfig() {
        CollectorConfig.init();
        GeneratorConfig.init();
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider lookupProvider) {
        dataGen.add(
                Dob.blockBuilder(GENERATOR_PART)
                        .blockTags(List.of(BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .blockState(provider -> DataGenHelper.registerGeneratorPart(GENERATOR_PART.block(), provider))
                        .parentedItem()
                        .standardLoot(ITEM_GENERATOR_PART_DATA.get())
                        .ironPickaxeTags()
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .define('X', ConventionalItemTags.GOLD_INGOTS)
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "XRX", "imi", "PRP"),
                Dob.blockBuilder(ENERGY_COLLECTOR)
                        .blockTags(List.of(BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .blockState(provider -> provider.simpleBlock(ENERGY_COLLECTOR.block().get(), provider.models().withExistingParent("energy_collector", Identifier.fromNamespaceAndPath(DeepResonance.MODID, "collector")).texture("collector_texture", "deepresonance:block/energy_collector")
                                .texture("particle", "deepresonance:block/energy_collector")))
                        .parentedItem()
                        .simpleLoot()
                        .ironPickaxeTags()
                        .shaped(builder -> builder
                                        .define('X', ConventionalItemTags.GOLD_INGOTS)
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .define('q', Items.QUARTZ)
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "PdP", "qmq", "XXX"),
                Dob.blockBuilder(GENERATOR_CONTROLLER)
                        .blockTags(List.of(BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .blockState(provider -> {
                            provider.horizontalOrientedBlock(GeneratorModule.GENERATOR_CONTROLLER.block().get(), (state, builder) -> {
                                if (state.getValue(BlockStateProperties.POWERED)) {
                                    builder.modelFile(provider.frontBasedModel(provider.name(state.getBlock()), Identifier.fromNamespaceAndPath(DeepResonance.MODID, "block/generator_controller_on"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
                                } else {
                                    builder.modelFile(provider.frontBasedModel(provider.name(state.getBlock()), Identifier.fromNamespaceAndPath(DeepResonance.MODID, "block/generator_controller"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
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
