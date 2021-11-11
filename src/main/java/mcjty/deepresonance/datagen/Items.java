package mcjty.deepresonance.datagen;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.lib.datagen.BaseItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DeepResonance.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        itemGenerated(CoreModule.FILTER_MATERIAL_ITEM.get(), "item/filter_material");
        itemGenerated(CoreModule.LIQUID_INJECTOR_ITEM.get(), "item/liquid_injector");
        itemGenerated(CoreModule.RESONATING_PLATE_ITEM.get(), "item/resonating_plate");
        itemGenerated(CoreModule.SPENT_FILTER_ITEM.get(), "item/spent_filter_material");
        itemGenerated(RadiationModule.RADIATION_SUIT_HELMET.get(), "item/radiation_suit_helmet");
        itemGenerated(RadiationModule.RADIATION_SUIT_CHESTPLATE.get(), "item/radiation_suit_chestplate");
        itemGenerated(RadiationModule.RADIATION_SUIT_LEGGINGS.get(), "item/radiation_suit_leggings");
        itemGenerated(RadiationModule.RADIATION_SUIT_BOOTS.get(), "item/radiation_suit_boots");

        cubeAll(name(CoreModule.MACHINE_FRAME_ITEM.get()), modLoc("block/machine_side"));

//        parentedItem(TankModule.TANK_ITEM.get(), BUILTIN_ENTITY); // @todo 1.16
        parentedBlock(CoreModule.RESONATING_ORE_STONE_BLOCK.get());
        parentedBlock(CoreModule.RESONATING_ORE_NETHER_BLOCK.get());
        parentedBlock(CoreModule.RESONATING_ORE_END_BLOCK.get());
        parentedBlock(RadiationModule.POISONED_DIRT_BLOCK.get());
        parentedBlock(RadiationModule.DENSE_GLASS_BLOCK.get());
        parentedBlock(RadiationModule.DENSE_OBSIDIAN_BLOCK.get());
        parentedBlock(CoreModule.RESONATING_PLATE_BLOCK_BLOCK.get());
        parentedBlock(MachinesModule.VALVE_BLOCK.get());
        parentedBlock(MachinesModule.SMELTER_BLOCK.get());
        parentedBlock(MachinesModule.PURIFIER_BLOCK.get());
//        parentedBlock(PulserModule.PULSER_BLOCK.get());
        parentedItem(MachinesModule.LENS_ITEM.get(), "block/lens_mc");
        parentedBlock(MachinesModule.LASER_BLOCK.get());
        parentedBlock(MachinesModule.CRYSTALLIZER_BLOCK.get());
        parentedBlock(GeneratorModule.ENERGY_COLLECTOR_BLOCK.get());
        parentedBlock(GeneratorModule.GENERATOR_CONTROLLER_BLOCK.get());
//        parentedItem(GeneratorModule.GENERATOR_PART_ITEM.get(), "block/generator_part_side");
        parentedBlock(GeneratorModule.GENERATOR_PART_BLOCK.get());
    }

}
