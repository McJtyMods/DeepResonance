package mcjty.deepresonance.datagen;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.lib.datagen.BaseLootTableProvider;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        addSimpleTable(CoreModule.RESONATING_ORE_DEEPSLATE_BLOCK.get());
        addSimpleTable(CoreModule.RESONATING_ORE_STONE_BLOCK.get());
        addSimpleTable(CoreModule.RESONATING_ORE_NETHER_BLOCK.get());
        addSimpleTable(CoreModule.RESONATING_ORE_END_BLOCK.get());
        addSimpleTable(CoreModule.RESONATING_PLATE_BLOCK_BLOCK.get());

        addSimpleTable(RadiationModule.POISONED_DIRT_BLOCK.get());
        addSimpleTable(RadiationModule.DENSE_GLASS_BLOCK.get());    // @todo 1.16 silk touch!
        addSimpleTable(RadiationModule.DENSE_OBSIDIAN_BLOCK.get());

//        addSimpleTable(PulserModule.PULSER_BLOCK.get());
        addSimpleTable(MachinesModule.LENS_BLOCK.get());
        addSimpleTable(GeneratorModule.ENERGY_COLLECTOR_BLOCK.get());
        addSimpleTable(GeneratorModule.GENERATOR_CONTROLLER_BLOCK.get());

        addStandardTable(MachinesModule.CRYSTALLIZER_BLOCK.get(), MachinesModule.TYPE_CRYSTALIZER.get());
        addStandardTable(MachinesModule.SMELTER_BLOCK.get(), MachinesModule.TYPE_SMELTER.get());
        addStandardTable(MachinesModule.PURIFIER_BLOCK.get(), MachinesModule.TYPE_PURIFIER.get());
        addStandardTable(GeneratorModule.GENERATOR_PART_BLOCK.get(), GeneratorModule.TYPE_GENERATOR_PART.get());
        addStandardTable(MachinesModule.VALVE_BLOCK.get(), MachinesModule.TYPE_VALVE.get());
        addStandardTable(MachinesModule.LASER_BLOCK.get(), MachinesModule.TYPE_LASER.get());
        addStandardTable(CoreModule.RESONATING_CRYSTAL_GENERATED.get(), CoreModule.TYPE_RESONATING_CRYSTAL.get());
        addStandardTable(CoreModule.RESONATING_CRYSTAL_GENERATED_EMPTY.get(), CoreModule.TYPE_RESONATING_CRYSTAL.get());
        addStandardTable(CoreModule.RESONATING_CRYSTAL_NATURAL.get(), CoreModule.TYPE_RESONATING_CRYSTAL.get());
        addStandardTable(CoreModule.RESONATING_CRYSTAL_NATURAL_EMPTY.get(), CoreModule.TYPE_RESONATING_CRYSTAL.get());
        addStandardTable(TankModule.TANK_BLOCK.get(), TankModule.TYPE_TANK.get());
    }

}
