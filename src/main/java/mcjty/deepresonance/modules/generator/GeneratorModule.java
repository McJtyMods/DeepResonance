package mcjty.deepresonance.modules.generator;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.block.*;
import mcjty.deepresonance.modules.generator.client.ClientSetup;
import mcjty.deepresonance.modules.generator.client.CollectorRenderer;
import mcjty.deepresonance.modules.generator.util.CollectorConfig;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.varia.SoundTools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.deepresonance.datagen.BlockStates.*;
import static mcjty.deepresonance.setup.Registration.TILES;

public class GeneratorModule implements IModule {

    public static final RegistryObject<Block> ENERGY_COLLECTOR_BLOCK = Registration.BLOCKS.register("energy_collector", EnergyCollectorBlock::new);
    public static final RegistryObject<Item> ENERGY_COLLECTOR_ITEM = Registration.fromBlock(ENERGY_COLLECTOR_BLOCK);
    public static final RegistryObject<BlockEntityType<EnergyCollectorTileEntity>> TYPE_ENERGY_COLLECTOR = TILES.register("energy_collector", () -> BlockEntityType.Builder.of(EnergyCollectorTileEntity::new, ENERGY_COLLECTOR_BLOCK.get()).build(null));

    public static final RegistryObject<Block> GENERATOR_CONTROLLER_BLOCK = Registration.BLOCKS.register("generator_controller", GeneratorControllerBlock::new);
    public static final RegistryObject<Item> GENERATOR_CONTROLLER_ITEM = Registration.fromBlock(GENERATOR_CONTROLLER_BLOCK);
    public static final RegistryObject<BlockEntityType<GeneratorControllerTileEntity>> TYPE_GENERATOR_CONTROLLER = TILES.register("generator_controller", () -> BlockEntityType.Builder.of(GeneratorControllerTileEntity::new, GENERATOR_CONTROLLER_BLOCK.get()).build(null));

    public static final RegistryObject<Block> GENERATOR_PART_BLOCK = Registration.BLOCKS.register("generator_part", GeneratorPartBlock::new);
    public static final RegistryObject<Item> GENERATOR_PART_ITEM = Registration.fromBlock(GENERATOR_PART_BLOCK);
    public static final RegistryObject<BlockEntityType<GeneratorPartTileEntity>> TYPE_GENERATOR_PART = TILES.register("generator_part", () -> BlockEntityType.Builder.of(GeneratorPartTileEntity::new, GENERATOR_PART_BLOCK.get()).build(null));

    public static final RegistryObject<SoundEvent> STARTUP_SOUND = Registration.SOUNDS.register("engine_start", () -> SoundTools.createSoundEvent(new ResourceLocation(DeepResonance.MODID, "engine_start")));
    public static final RegistryObject<SoundEvent> LOOP_SOUND = Registration.SOUNDS.register("engine_loop", () -> SoundTools.createSoundEvent(new ResourceLocation(DeepResonance.MODID, "engine_loop")));
    public static final RegistryObject<SoundEvent> SHUTDOWN_SOUND = Registration.SOUNDS.register("engine_shutdown", () -> SoundTools.createSoundEvent(new ResourceLocation(DeepResonance.MODID, "engine_shutdown")));

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        ClientSetup.initClient();
        CollectorRenderer.register();
    }

    @Override
    public void initConfig() {
        CollectorConfig.init();
        GeneratorConfig.init();
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(GENERATOR_PART_BLOCK)
                        .blockState(provider -> DataGenHelper.registerGeneratorPart(GENERATOR_PART_BLOCK, provider))
                        .parentedItem()
                        .standardLoot(TYPE_GENERATOR_PART)
                        .ironPickaxeTags()
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .define('X', Tags.Items.INGOTS_GOLD)
                                        .define('m', CoreModule.MACHINE_FRAME_ITEM.get())
                                        .unlockedBy("has_machine_frame", DataGen.has(CoreModule.MACHINE_FRAME_ITEM.get())),
                                "XRX", "imi", "PRP"),
                Dob.blockBuilder(ENERGY_COLLECTOR_BLOCK)
                        .blockState(provider -> provider.simpleBlock(ENERGY_COLLECTOR_BLOCK.get(), provider.models().withExistingParent("energy_collector", new ResourceLocation(DeepResonance.MODID, "collector")).texture("collector_texture", "deepresonance:block/energy_collector")
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
                Dob.blockBuilder(GENERATOR_CONTROLLER_BLOCK)
                        .blockState(provider -> {
                            provider.horizontalOrientedBlock(GeneratorModule.GENERATOR_CONTROLLER_BLOCK.get(), (state, builder) -> {
                                if (state.getValue(BlockStateProperties.POWERED)) {
                                    builder.modelFile(provider.frontBasedModel(provider.name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/generator_controller_on"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
                                } else {
                                    builder.modelFile(provider.frontBasedModel(provider.name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/generator_controller"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
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
