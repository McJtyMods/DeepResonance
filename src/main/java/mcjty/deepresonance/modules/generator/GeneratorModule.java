package mcjty.deepresonance.modules.generator;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.generator.block.BlockCollector;
import mcjty.deepresonance.modules.generator.client.CollectorTESR;
import mcjty.deepresonance.modules.generator.tile.TileEntityEnergyCollector;
import mcjty.deepresonance.modules.generator.tile.TileEntityGeneratorController;
import mcjty.deepresonance.modules.generator.tile.TileEntityGeneratorPart;
import mcjty.deepresonance.modules.generator.util.CollectorConfig;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.modules.IModule;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.deepresonance.setup.Registration.TILES;

/**
 * Created by Elec332 on 30-7-2020
 * <p>
 * TODO: Generator sounds
 */
public class GeneratorModule implements IModule {

    public static final RegistryObject<Block> ENERGY_COLLECTOR_BLOCK = Registration.block("energy_collector", TileEntityEnergyCollector::new, BlockCollector::new);
    public static final RegistryObject<Item> ENERGY_COLLECTOR_ITEM = Registration.fromBlock(ENERGY_COLLECTOR_BLOCK);
    public static final RegistryObject<TileEntityType<TileEntityEnergyCollector>> TYPE_ENERGY_COLLECTOR = TILES.register("energy_collector", () -> TileEntityType.Builder.of(TileEntityEnergyCollector::new, ENERGY_COLLECTOR_BLOCK.get()).build(null));

    public static final RegistryObject<Block> GENERATOR_CONTROLLER_BLOCK = Registration.defaultBlock("generator_controller", TileEntityGeneratorController::new, state -> state.setValue(BlockStateProperties.POWERED, false), BlockStateProperties.POWERED);
    public static final RegistryObject<Item> GENERATOR_CONTROLLER_ITEM = Registration.fromBlock(GENERATOR_CONTROLLER_BLOCK);
    public static final RegistryObject<TileEntityType<TileEntityGeneratorController>> TYPE_GENERATOR_CONTROLLER = TILES.register("generator_controller", () -> TileEntityType.Builder.of(TileEntityGeneratorController::new, GENERATOR_CONTROLLER_BLOCK.get()).build(null));

    public static final RegistryObject<Block> GENERATOR_PART_BLOCK = Registration.nonRotatingBlock("generator_part", TileEntityGeneratorPart::new, state -> state.setValue(BlockStateProperties.POWERED, false).setValue(BlockStateProperties.UP, false).setValue(BlockStateProperties.DOWN, false), BlockStateProperties.POWERED, BlockStateProperties.UP, BlockStateProperties.DOWN);
    public static final RegistryObject<Item> GENERATOR_PART_ITEM = Registration.fromBlock(GENERATOR_PART_BLOCK);
    public static final RegistryObject<TileEntityType<TileEntityGeneratorPart>> TYPE_GENERATOR_PART = TILES.register("generator_part", () -> TileEntityType.Builder.of(TileEntityGeneratorPart::new, GENERATOR_PART_BLOCK.get()).build(null));

    public static CollectorConfig collectorConfig;
    public static GeneratorConfig generatorConfig;

    @Override
    public void init(FMLCommonSetupEvent event) {
        DeepResonance.logger.info("Registering generator grid handler");
        // @todo 1.16
//        ElecCoreRegistrar.GRIDHANDLERS.register(new DynamicMultiblockGridHandler<>(t -> t instanceof AbstractTileEntityGeneratorComponent, t -> new SimpleDynamicMultiblockTileLink<>((AbstractTileEntityGeneratorComponent) t, AbstractTileEntityGeneratorComponent::setGrid), GeneratorGrid::new));
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        CollectorTESR.register();
    }

    @Override
    public void initConfig() {
        // @todo 1.16
//        Config.configuration.configureSubConfig("generator", "Generator module settings", config -> {
//            collectorConfig = config.registerConfig(CollectorConfig::new, "collector", "Collector settings");
//            generatorConfig = config.registerConfig(GeneratorConfig::new, "generator_settings", "Generator settings");
//        });
    }
}
