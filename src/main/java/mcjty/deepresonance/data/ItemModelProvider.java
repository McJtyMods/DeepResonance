package mcjty.deepresonance.data;

import elec332.core.data.AbstractItemModelProvider;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.pulser.PulserModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

/**
 * Created by Elec332 on 10-1-2020
 */
class ItemModelProvider extends AbstractItemModelProvider {

    ItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DeepResonance.MODID, existingFileHelper);
    }

    @Override
    protected void registerItemModels() {

        //Items
        simpleModel(CoreModule.FILTER_MATERIAL_ITEM);
        simpleModel(CoreModule.LIQUID_INJECTOR_ITEM);
        simpleModel(CoreModule.RESONATING_PLATE_ITEM);
        simpleModel(CoreModule.SPENT_FILTER_ITEM);
        simpleModel(RadiationModule.RADIATION_SUIT_HELMET);
        simpleModel(RadiationModule.RADIATION_SUIT_CHESTPLATE);
        simpleModel(RadiationModule.RADIATION_SUIT_LEGGINGS);
        simpleModel(RadiationModule.RADIATION_SUIT_BOOTS);

        cubeAll(CoreModule.MACHINE_FRAME_ITEM, "block/machine_side");

        //Blocks
        parentedModel(TankModule.TANK_ITEM, BUILTIN_ENTITY);
        parentedModel(CoreModule.RESONATING_ORE_STONE_BLOCK);
        parentedModel(CoreModule.RESONATING_ORE_NETHER_BLOCK);
        parentedModel(CoreModule.RESONATING_ORE_END_BLOCK);
        parentedModel(RadiationModule.POISONED_DIRT_BLOCK);
        parentedModel(RadiationModule.DENSE_GLASS_BLOCK);
        parentedModel(RadiationModule.DENSE_OBSIDIAN_BLOCK);
        parentedModel(CoreModule.RESONATING_PLATE_BLOCK_BLOCK);
        parentedModel(MachinesModule.VALVE_BLOCK);
        parentedModel(MachinesModule.SMELTER_BLOCK);
        parentedModel(MachinesModule.PURIFIER_BLOCK);
        parentedModel(PulserModule.PULSER_BLOCK);
        parentedModel(MachinesModule.LENS_ITEM, "block/lens_mc");
        parentedModel(MachinesModule.LASER_BLOCK);
        parentedModel(MachinesModule.CRYSTALLIZER_BLOCK);
        parentedModel(GeneratorModule.ENERGY_COLLECTOR_BLOCK);
        parentedModel(GeneratorModule.GENERATOR_CONTROLLER_BLOCK);
        parentedModel(GeneratorModule.GENERATOR_PART_ITEM, "block/generator_part_side");
    }

}
