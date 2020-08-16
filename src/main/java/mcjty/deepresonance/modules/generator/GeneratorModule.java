package mcjty.deepresonance.modules.generator;

import elec332.core.grid.multiblock.DynamicMultiblockGridHandler;
import elec332.core.grid.multiblock.SimpleDynamicMultiblockTileLink;
import elec332.core.handler.ElecCoreRegistrar;
import elec332.core.util.BlockProperties;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.generator.block.BlockCollector;
import mcjty.deepresonance.modules.generator.grid.GeneratorGrid;
import mcjty.deepresonance.modules.generator.tile.AbstractTileEntityGeneratorComponent;
import mcjty.deepresonance.modules.generator.tile.TileEntityEnergyCollector;
import mcjty.deepresonance.modules.generator.tile.TileEntityGeneratorController;
import mcjty.deepresonance.modules.generator.tile.TileEntityGeneratorPart;
import mcjty.deepresonance.modules.generator.util.CollectorConfig;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Created by Elec332 on 30-7-2020
 * <p>
 * TODO: Generator sounds
 */
public class GeneratorModule {

    public static final RegistryObject<Block> ENERGY_COLLECTOR_BLOCK = DeepResonance.block("energy_collector", TileEntityEnergyCollector::new, BlockCollector::new);
    public static final RegistryObject<Block> GENERATOR_CONTROLLER_BLOCK = DeepResonance.defaultBlock("generator_controller", TileEntityGeneratorController::new, state -> state.with(BlockProperties.ON, false), BlockProperties.ON);
    public static final RegistryObject<Block> GENERATOR_PART_BLOCK = DeepResonance.nonRotatingBlock("generator_part", TileEntityGeneratorPart::new, state -> state.with(BlockProperties.ON, false).with(BlockProperties.UP, false).with(BlockProperties.DOWN, false), BlockProperties.ON, BlockProperties.UP, BlockProperties.DOWN);

    public static final RegistryObject<Item> ENERGY_COLLECTOR_ITEM = DeepResonance.fromBlock(ENERGY_COLLECTOR_BLOCK);
    public static final RegistryObject<Item> GENERATOR_CONTROLLER_ITEM = DeepResonance.fromBlock(GENERATOR_CONTROLLER_BLOCK);
    public static final RegistryObject<Item> GENERATOR_PART_ITEM = DeepResonance.fromBlock(GENERATOR_PART_BLOCK);

    public static CollectorConfig collectorConfig;
    public static GeneratorConfig generatorConfig;

    public GeneratorModule(IEventBus eventBus) {
        DeepResonance.configuration.configureSubConfig("generator", "Generator module settings", config -> {
            collectorConfig = config.registerConfig(CollectorConfig::new, "collector", "Collector settings");
            generatorConfig = config.registerConfig(GeneratorConfig::new, "generator_settings", "Generator settings");
        });
        eventBus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        DeepResonance.logger.info("Registering generator grid handler");
        ElecCoreRegistrar.GRIDHANDLERS.register(new DynamicMultiblockGridHandler<>(t -> t instanceof AbstractTileEntityGeneratorComponent, t -> new SimpleDynamicMultiblockTileLink<>((AbstractTileEntityGeneratorComponent) t, AbstractTileEntityGeneratorComponent::setGrid), GeneratorGrid::new));
    }

}
