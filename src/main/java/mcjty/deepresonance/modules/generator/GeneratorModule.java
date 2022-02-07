package mcjty.deepresonance.modules.generator;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.generator.block.*;
import mcjty.deepresonance.modules.generator.client.ClientSetup;
import mcjty.deepresonance.modules.generator.client.CollectorRenderer;
import mcjty.deepresonance.modules.generator.util.CollectorConfig;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.modules.IModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

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

    public static final RegistryObject<SoundEvent> STARTUP_SOUND = Registration.SOUNDS.register("engine_start", () -> new SoundEvent(new ResourceLocation(DeepResonance.MODID, "engine_start")));
    public static final RegistryObject<SoundEvent> LOOP_SOUND = Registration.SOUNDS.register("engine_loop", () -> new SoundEvent(new ResourceLocation(DeepResonance.MODID, "engine_loop")));
    public static final RegistryObject<SoundEvent> SHUTDOWN_SOUND = Registration.SOUNDS.register("engine_shutdown", () -> new SoundEvent(new ResourceLocation(DeepResonance.MODID, "engine_shutdown")));

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
}
