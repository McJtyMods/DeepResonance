package mcjty.deepresonance.datagen;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.lib.datagen.BaseItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import static mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem.RADIATION_PROPERTY;

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

        parentedItem(TankModule.TANK_ITEM.get(), "block/tank");
        parentedBlock(CoreModule.RESONATING_CRYSTAL_NATURAL.get(), "block/crystal_full");
        parentedBlock(CoreModule.RESONATING_CRYSTAL_NATURAL_EMPTY.get(), "block/crystal_empty");
        parentedBlock(CoreModule.RESONATING_CRYSTAL_GENERATED.get(), "block/crystal_full_pure");
        parentedBlock(CoreModule.RESONATING_CRYSTAL_GENERATED_EMPTY.get(), "block/crystal_empty_pure");

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
        parentedBlock(GeneratorModule.GENERATOR_PART_BLOCK.get());

        getBuilder(RadiationModule.RADIATION_MONITOR.get().getRegistryName().getPath())
                .parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", "item/monitor/radiationmonitoritem")
                .override().predicate(RADIATION_PROPERTY, 0).model(createMonitorModel(0)).end()
                .override().predicate(RADIATION_PROPERTY, 1).model(createMonitorModel(1)).end()
                .override().predicate(RADIATION_PROPERTY, 2).model(createMonitorModel(2)).end()
                .override().predicate(RADIATION_PROPERTY, 3).model(createMonitorModel(3)).end()
                .override().predicate(RADIATION_PROPERTY, 4).model(createMonitorModel(4)).end()
                .override().predicate(RADIATION_PROPERTY, 5).model(createMonitorModel(5)).end()
                .override().predicate(RADIATION_PROPERTY, 6).model(createMonitorModel(6)).end()
                .override().predicate(RADIATION_PROPERTY, 7).model(createMonitorModel(7)).end()
                .override().predicate(RADIATION_PROPERTY, 8).model(createMonitorModel(8)).end()
                .override().predicate(RADIATION_PROPERTY, 9).model(createMonitorModel(9)).end()
        ;
    }

    private ItemModelBuilder createMonitorModel(int suffix) {
        return getBuilder("radiationmonitoritem" + suffix).parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", "item/monitor/radiationmonitoritem" + suffix);
    }


}
