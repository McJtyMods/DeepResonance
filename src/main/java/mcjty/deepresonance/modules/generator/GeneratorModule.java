package mcjty.deepresonance.modules.generator;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.generator.block.*;
import mcjty.deepresonance.modules.generator.client.CollectorTESR;
import mcjty.deepresonance.modules.generator.util.CollectorConfig;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.modules.IModule;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.deepresonance.setup.Registration.TILES;

/**
 * TODO: Generator sounds
 */
public class GeneratorModule implements IModule {

    public static final RegistryObject<Block> ENERGY_COLLECTOR_BLOCK = Registration.BLOCKS.register("energy_collector", EnergyCollectorBlock::new);
    public static final RegistryObject<Item> ENERGY_COLLECTOR_ITEM = Registration.fromBlock(ENERGY_COLLECTOR_BLOCK);
    public static final RegistryObject<TileEntityType<EnergyCollectorTileEntity>> TYPE_ENERGY_COLLECTOR = TILES.register("energy_collector", () -> TileEntityType.Builder.of(EnergyCollectorTileEntity::new, ENERGY_COLLECTOR_BLOCK.get()).build(null));

    public static final RegistryObject<Block> GENERATOR_CONTROLLER_BLOCK = Registration.BLOCKS.register("generator_controller", GeneratorControllerBlock::new);
    public static final RegistryObject<Item> GENERATOR_CONTROLLER_ITEM = Registration.fromBlock(GENERATOR_CONTROLLER_BLOCK);
    public static final RegistryObject<TileEntityType<GeneratorControllerTileEntity>> TYPE_GENERATOR_CONTROLLER = TILES.register("generator_controller", () -> TileEntityType.Builder.of(GeneratorControllerTileEntity::new, GENERATOR_CONTROLLER_BLOCK.get()).build(null));

    public static final RegistryObject<Block> GENERATOR_PART_BLOCK = Registration.BLOCKS.register("generator_part", GeneratorPartBlock::new);
    public static final RegistryObject<Item> GENERATOR_PART_ITEM = Registration.fromBlock(GENERATOR_PART_BLOCK);
    public static final RegistryObject<TileEntityType<GeneratorPartTileEntity>> TYPE_GENERATOR_PART = TILES.register("generator_part", () -> TileEntityType.Builder.of(GeneratorPartTileEntity::new, GENERATOR_PART_BLOCK.get()).build(null));

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
        CollectorConfig.init();
        GeneratorConfig.init();
    }
}
