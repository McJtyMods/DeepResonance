package mcjty.deepresonance.data;

import mcjty.lib.datagen.BaseLootTableProvider;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        // @todo 1.16
//        addBlockLootTable(new AbstractBlockLootTables(DeepResonance.MODID) {
//
//            @Override
//            protected void registerBlockTables() {
//                registerDropSelfLootTable(CoreModule.RESONATING_ORE_STONE_BLOCK);
//                registerDropSelfLootTable(CoreModule.RESONATING_ORE_NETHER_BLOCK);
//                registerDropSelfLootTable(CoreModule.RESONATING_ORE_END_BLOCK);
//                registerDropSelfLootTable(CoreModule.RESONATING_PLATE_BLOCK_BLOCK);
//
//                registerDropSelfLootTable(RadiationModule.POISONED_DIRT_BLOCK);
//                registerSilkTouch(RadiationModule.DENSE_GLASS_BLOCK.get());
//                registerDropSelfLootTable(RadiationModule.DENSE_OBSIDIAN_BLOCK);
//
//                registerDropSelfLootTable(MachinesModule.VALVE_BLOCK);
//                registerDropSelfLootTable(MachinesModule.SMELTER_BLOCK);
//                registerDropSelfLootTable(MachinesModule.PURIFIER_BLOCK);
//                registerDropSelfLootTable(PulserModule.PULSER_BLOCK);
//                registerEmptyLootTable(MachinesModule.LENS_BLOCK.get());
//                registerDropSelfLootTable(MachinesModule.LASER_BLOCK);
//                registerDropSelfLootTable(MachinesModule.CRYSTALLIZER_BLOCK);
//                registerDropSelfLootTable(GeneratorModule.ENERGY_COLLECTOR_BLOCK);
//                registerDropSelfLootTable(GeneratorModule.GENERATOR_CONTROLLER_BLOCK);
//                registerDropSelfLootTable(GeneratorModule.GENERATOR_PART_BLOCK);
//
//                registerDropSelfLootTable(CoreModule.RESONATING_CRYSTAL_BLOCK, builder -> builder.acceptFunction(copyAllTileTags()));
//                registerDropSelfLootTable(TankModule.TANK_BLOCK, builder -> builder.acceptFunction(copyAllTileTags()));
//            }
//
//        });
    }

}
