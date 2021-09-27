package mcjty.deepresonance.data;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.datagen.BaseItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DeepResonance.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // @todo 1.16
//        //Items
//        simpleModel(CoreModule.FILTER_MATERIAL_ITEM);
//        simpleModel(CoreModule.LIQUID_INJECTOR_ITEM);
//        simpleModel(CoreModule.RESONATING_PLATE_ITEM);
//        simpleModel(CoreModule.SPENT_FILTER_ITEM);
//        simpleModel(RadiationModule.RADIATION_SUIT_HELMET);
//        simpleModel(RadiationModule.RADIATION_SUIT_CHESTPLATE);
//        simpleModel(RadiationModule.RADIATION_SUIT_LEGGINGS);
//        simpleModel(RadiationModule.RADIATION_SUIT_BOOTS);
//
//        cubeAll(CoreModule.MACHINE_FRAME_ITEM, "block/machine_side");
//
//        //Blocks
//        parentedModel(TankModule.TANK_ITEM, BUILTIN_ENTITY);
//        parentedModel(CoreModule.RESONATING_ORE_STONE_BLOCK);
//        parentedModel(CoreModule.RESONATING_ORE_NETHER_BLOCK);
//        parentedModel(CoreModule.RESONATING_ORE_END_BLOCK);
//        parentedModel(RadiationModule.POISONED_DIRT_BLOCK);
//        parentedModel(RadiationModule.DENSE_GLASS_BLOCK);
//        parentedModel(RadiationModule.DENSE_OBSIDIAN_BLOCK);
//        parentedModel(CoreModule.RESONATING_PLATE_BLOCK_BLOCK);
//        parentedModel(MachinesModule.VALVE_BLOCK);
//        parentedModel(MachinesModule.SMELTER_BLOCK);
//        parentedModel(MachinesModule.PURIFIER_BLOCK);
//        parentedModel(PulserModule.PULSER_BLOCK);
//        parentedModel(MachinesModule.LENS_ITEM, "block/lens_mc");
//        parentedModel(MachinesModule.LASER_BLOCK);
//        parentedModel(MachinesModule.CRYSTALLIZER_BLOCK);
//        parentedModel(GeneratorModule.ENERGY_COLLECTOR_BLOCK);
//        parentedModel(GeneratorModule.GENERATOR_CONTROLLER_BLOCK);
//        parentedModel(GeneratorModule.GENERATOR_PART_ITEM, "block/generator_part_side");
    }

}
