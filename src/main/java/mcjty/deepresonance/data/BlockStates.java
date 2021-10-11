package mcjty.deepresonance.data;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalBlock;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.client.TankRenderer;
import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BaseBlockStateProvider {

    private final static ResourceLocation DEFAULT_TOP = new ResourceLocation(DeepResonance.MODID, "block/machine_top");
    private final static ResourceLocation DEFAULT_SIDE = new ResourceLocation(DeepResonance.MODID, "block/machine_side");
    private final static ResourceLocation DEFAULT_BOTTOM = new ResourceLocation(DeepResonance.MODID, "block/machine_bottom");

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, DeepResonance.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerCrystalModel();
        simpleBlock(TankModule.TANK_BLOCK.get(), models().cubeBottomTop("tank", TankRenderer.SIDE_TEXTURE, TankRenderer.BOTTOM_TEXTURE, TankRenderer.TOP_TEXTURE));
        simpleBlock(CoreModule.RESONATING_ORE_STONE_BLOCK.get());
        simpleBlock(CoreModule.RESONATING_ORE_NETHER_BLOCK.get());
        simpleBlock(CoreModule.RESONATING_ORE_END_BLOCK.get());
        simpleBlock(RadiationModule.POISONED_DIRT_BLOCK.get());
        simpleBlock(RadiationModule.DENSE_GLASS_BLOCK.get());
        simpleBlock(RadiationModule.DENSE_OBSIDIAN_BLOCK.get());
        simpleBlock(CoreModule.RESONATING_PLATE_BLOCK_BLOCK.get());
        simpleBlock(MachinesModule.VALVE_BLOCK.get(), models().cubeBottomTop(name(MachinesModule.VALVE_BLOCK.get()), new ResourceLocation(DeepResonance.MODID, "block/valve"), DEFAULT_BOTTOM, DEFAULT_TOP));
        horizontalOrientedBlock(MachinesModule.SMELTER_BLOCK.get(), (state, builder) -> {
            if (state.getValue(BlockStateProperties.POWERED)) {
                builder.modelFile(frontBasedModel(name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/smelter_active"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
            } else {
                builder.modelFile(frontBasedModel(name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/smelter"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
            }
        });
        horizontalOrientedBlock(MachinesModule.PURIFIER_BLOCK.get(), (state, builder) -> builder.modelFile(frontBasedModel(name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/purifier"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM)));
        simpleBlock(MachinesModule.LENS_BLOCK.get(), models().withExistingParent("lens_mc", new ResourceLocation(DeepResonance.MODID, "lens")).texture("lens_texture", "deepresonance:block/lens"));
//        simpleFront(PulserModule.PULSER_BLOCK);
        horizontalOrientedBlock(MachinesModule.LASER_BLOCK.get(), createLaserModel());
        horizontalOrientedBlock(MachinesModule.CRYSTALLIZER_BLOCK.get(), createCrystallizerModel());
        simpleBlock(GeneratorModule.ENERGY_COLLECTOR_BLOCK.get(), models().withExistingParent("energy_collector", new ResourceLocation(DeepResonance.MODID, "collector")).texture("collector_texture", "deepresonance:block/energy_collector"));
        horizontalOrientedBlock(GeneratorModule.GENERATOR_CONTROLLER_BLOCK.get(), (state, builder) -> {
            if (state.getValue(BlockStateProperties.POWERED)) {
                builder.modelFile(frontBasedModel(name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/generator_controller_on"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
            } else {
                builder.modelFile(frontBasedModel(name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/generator_controller"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));            }
        });
        variantBlock(GeneratorModule.GENERATOR_PART_BLOCK.get(), state -> {
            String extra = "";
            if (state.getValue(BlockStateProperties.UP)) {
                extra += "_up";
            }
            if (state.getValue(BlockStateProperties.DOWN)) {
                extra += "_down";
            }
            if (state.getValue(BlockStateProperties.POWERED)) {
                extra += "_on";
            }
            ResourceLocation id = new ResourceLocation(DeepResonance.MODID, "block/generator_part_side" + extra);
            return models().cube("generator_part" + extra, DEFAULT_BOTTOM, DEFAULT_TOP, id, id, id, id);
        });
    }

    private void registerCrystalModel() {
        ResourceLocation crystal = new ResourceLocation(DeepResonance.MODID, "crystal");
        ResourceLocation crystalGenerated = new ResourceLocation(DeepResonance.MODID, "crystal_generated");

        ModelFile empty = models().withExistingParent("crystal_empty", crystal).texture("crystal_texture", "deepresonance:block/empty_crystal");
        ModelFile emptyGenerated = models().withExistingParent("crystal_empty_pure", crystalGenerated).texture("crystal_texture", "deepresonance:block/empty_crystal");
        ModelFile full = models().withExistingParent("crystal_full", crystal).texture("crystal_texture", "deepresonance:block/crystal");
        ModelFile fullGenerated = models().withExistingParent("crystal_full_pure", crystalGenerated).texture("crystal_texture", "deepresonance:block/crystal");

        getVariantBuilder(CoreModule.RESONATING_CRYSTAL_BLOCK.get()).forAllStates(state -> {
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Boolean isEmpty = state.getValue(ResonatingCrystalBlock.EMPTY);
            Boolean isGenerated = state.getValue(ResonatingCrystalBlock.GENERATED);
            ModelFile model;
            if (isEmpty && isGenerated) {
                model = emptyGenerated;
            } else if (isEmpty) {
                model = empty;
            } else if (isGenerated) {
                model = fullGenerated;
            } else {
                model = full;
            }
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY((int) ((direction.toYRot() + 180) % 360))
                    .build();
        });
    }

    public BlockModelBuilder createCrystallizerModel() {
        BlockModelBuilder ret = models().cube("crystallizer", DEFAULT_BOTTOM, DEFAULT_TOP, DEFAULT_SIDE, new ResourceLocation(DeepResonance.MODID, "block/crystallizer"), DEFAULT_SIDE, DEFAULT_SIDE);
        ModelBuilder<BlockModelBuilder>.ElementBuilder elem = ret.element();
        for (Direction direction : Direction.values()) {
            elem = elem.face(direction).cullface(direction).texture("#" + direction.getName()).end();
        }
        ret = elem.end();
        ret = ret.element().from(0, 0, 0).to(16, 7, 16).face(Direction.UP).texture("#" + Direction.UP.getName()).end().end();
        elem = ret.element().from(16, 16, 16).to(0, 0, 0);
        for (Direction direction : Direction.values()) {
            elem = elem.face(direction.getOpposite()).cullface(Direction.NORTH).texture("#" + Direction.UP.getName()).end();
        }
        return elem.end();
    }

    public BlockModelBuilder createLaserModel() {
        BlockModelBuilder ret = models().cube("laser", DEFAULT_BOTTOM, DEFAULT_TOP, new ResourceLocation(DeepResonance.MODID, "block/laser_back"), new ResourceLocation(DeepResonance.MODID, "block/laser"), new ResourceLocation(DeepResonance.MODID, "block/laser"), new ResourceLocation(DeepResonance.MODID, "block/laser"));
        ret.texture("back", new ResourceLocation(DeepResonance.MODID, "block/laser_back_color"));
        ModelBuilder<BlockModelBuilder>.ElementBuilder elem = ret.element();
        for (Direction direction : Direction.values()) {
            elem = elem.face(direction).cullface(direction).texture("#" + direction.getName()).end();
        }
        ret = elem.end();
        return ret.element().from(0, 0, 1).to(16, 16, 16).face(Direction.SOUTH).cullface(Direction.SOUTH).texture("#back").tintindex(1).end().end();
    }
//
//    @Override
//    protected ResourceLocation getDefaultTopLocation() {
//        return DEFAULT_TOP;
//    }
//
//    @Override
//    protected ResourceLocation getDefaultBottomLocation() {
//        return DEFAULT_BOTTOM;
//    }
//
//    @Override
//    protected ResourceLocation getDefaultSideLocation() {
//        return DEFAULT_SIDE;
//    }

}
