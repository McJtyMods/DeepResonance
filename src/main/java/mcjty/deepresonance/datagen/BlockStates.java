package mcjty.deepresonance.datagen;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.client.TankTESR;
import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BaseBlockStateProvider {

    private static final ResourceLocation DEFAULT_TOP = new ResourceLocation(DeepResonance.MODID, "block/machine_top");
    private static final ResourceLocation DEFAULT_SIDE = new ResourceLocation(DeepResonance.MODID, "block/machine_side");
    private static final ResourceLocation DEFAULT_BOTTOM = new ResourceLocation(DeepResonance.MODID, "block/machine_bottom");

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, DeepResonance.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerCrystalModel();
        registerTankModel();
        registerGeneratorPart();
        simpleBlock(CoreModule.RESONATING_ORE_STONE_BLOCK.get());
        simpleBlock(CoreModule.RESONATING_ORE_DEEPSLATE_BLOCK.get());
        simpleBlock(CoreModule.RESONATING_ORE_NETHER_BLOCK.get());
        simpleBlock(CoreModule.RESONATING_ORE_END_BLOCK.get());
        simpleBlock(RadiationModule.POISONED_DIRT_BLOCK.get());
        simpleBlockC(RadiationModule.DENSE_GLASS_BLOCK.get(), modelBuilder -> modelBuilder.renderType("cutout"));
        simpleBlock(RadiationModule.DENSE_OBSIDIAN_BLOCK.get());
        simpleBlock(CoreModule.RESONATING_PLATE_BLOCK_BLOCK.get());
        simpleBlock(MachinesModule.VALVE_BLOCK.get(), models().cubeBottomTop(name(MachinesModule.VALVE_BLOCK.get()), new ResourceLocation(DeepResonance.MODID, "block/valve"), DEFAULT_BOTTOM, DEFAULT_TOP));
        horizontalOrientedBlock(MachinesModule.SMELTER_BLOCK.get(), (state, builder) -> {
            if (state.getValue(BlockStateProperties.POWERED)) {
                builder.modelFile(frontBasedModel(name(state.getBlock())+"_active", new ResourceLocation(DeepResonance.MODID, "block/smelter_active"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
            } else {
                builder.modelFile(frontBasedModel(name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/smelter"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
            }
        });
        horizontalOrientedBlock(MachinesModule.PURIFIER_BLOCK.get(), (state, builder) -> builder.modelFile(frontBasedModel(name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/purifier"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM)));
        horizontalOrientedBlock(MachinesModule.LENS_BLOCK.get(), models()
                .withExistingParent("lens_mc", new ResourceLocation(DeepResonance.MODID, "lens"))
                .texture("lens_texture", "deepresonance:block/lens")
                .texture("particle", "deepresonance:block/lens"));
        horizontalOrientedBlock(MachinesModule.LASER_BLOCK.get(), createLaserModel());
        horizontalOrientedBlock(MachinesModule.CRYSTALLIZER_BLOCK.get(), createCrystallizerModel());
        simpleBlock(GeneratorModule.ENERGY_COLLECTOR_BLOCK.get(),
                models().withExistingParent("energy_collector", new ResourceLocation(DeepResonance.MODID, "collector")).texture("collector_texture", "deepresonance:block/energy_collector")
                        .texture("particle", "deepresonance:block/energy_collector"));
        horizontalOrientedBlock(GeneratorModule.GENERATOR_CONTROLLER_BLOCK.get(), (state, builder) -> {
            if (state.getValue(BlockStateProperties.POWERED)) {
                builder.modelFile(frontBasedModel(name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/generator_controller_on"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));
            } else {
                builder.modelFile(frontBasedModel(name(state.getBlock()), new ResourceLocation(DeepResonance.MODID, "block/generator_controller"), DEFAULT_SIDE, DEFAULT_TOP, DEFAULT_BOTTOM));            }
        });
    }

    private void registerGeneratorPart() {
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
            return models().cube("generator_part" + extra, DEFAULT_BOTTOM, DEFAULT_TOP, id, id, id, id)
                    .texture("particle", id);
        });
    }

    private void registerTankModel() {
        simpleBlock(TankModule.TANK_BLOCK.get(),
                models().cubeBottomTop("tank", TankTESR.TANK_SIDE, TankTESR.TANK_BOTTOM, TankTESR.TANK_TOP).renderType("translucent"));
    }

    private void registerCrystalModel() {
        ResourceLocation crystal = new ResourceLocation(DeepResonance.MODID, "crystal");
        ResourceLocation crystalGenerated = new ResourceLocation(DeepResonance.MODID, "crystal_generated");

        ModelFile emptyNatural = models().withExistingParent("crystal_empty", crystal).texture("crystal_texture", "deepresonance:block/empty_crystal")
                .texture("particle", "deepresonance:block/empty_crystal").renderType("translucent");
        ModelFile emptyGenerated = models().withExistingParent("crystal_empty_pure", crystalGenerated).texture("crystal_texture", "deepresonance:block/empty_crystal")
                .texture("particle", "deepresonance:block/empty_crystal").renderType("translucent");
        ModelFile fullNatural = models().withExistingParent("crystal_full", crystal).texture("crystal_texture", "deepresonance:block/crystal")
                .texture("particle", "deepresonance:block/crystal").renderType("translucent");
        ModelFile fullGenerated = models().withExistingParent("crystal_full_pure", crystalGenerated).texture("crystal_texture", "deepresonance:block/crystal")
                .texture("particle", "deepresonance:block/crystal").renderType("translucent");

        getVariantBuilder(CoreModule.RESONATING_CRYSTAL_GENERATED.get()).forAllStates(state -> {
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return ConfiguredModel.builder()
                    .modelFile(fullGenerated)
                    .rotationY((int) ((direction.toYRot() + 180) % 360))
                    .build();
        });
        getVariantBuilder(CoreModule.RESONATING_CRYSTAL_GENERATED_EMPTY.get()).forAllStates(state -> {
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return ConfiguredModel.builder()
                    .modelFile(emptyGenerated)
                    .rotationY((int) ((direction.toYRot() + 180) % 360))
                    .build();
        });
        getVariantBuilder(CoreModule.RESONATING_CRYSTAL_NATURAL.get()).forAllStates(state -> {
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return ConfiguredModel.builder()
                    .modelFile(fullNatural)
                    .rotationY((int) ((direction.toYRot() + 180) % 360))
                    .build();
        });
        getVariantBuilder(CoreModule.RESONATING_CRYSTAL_NATURAL_EMPTY.get()).forAllStates(state -> {
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return ConfiguredModel.builder()
                    .modelFile(emptyNatural)
                    .rotationY((int) ((direction.toYRot() + 180) % 360))
                    .build();
        });
    }

    public BlockModelBuilder createCrystallizerModel() {
        BlockModelBuilder ret = models().cube("crystallizer", DEFAULT_BOTTOM, DEFAULT_TOP, new ResourceLocation(DeepResonance.MODID, "block/crystallizer"), DEFAULT_SIDE, DEFAULT_SIDE, DEFAULT_SIDE);
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
        return elem.end()
                .texture("particle", "deepresonance:block/crystallizer")
                .renderType("translucent");
    }

    public BlockModelBuilder createLaserModel() {
        ResourceLocation laserTxt = new ResourceLocation(DeepResonance.MODID, "block/laser");
        ResourceLocation laserBackTxt = new ResourceLocation(DeepResonance.MODID, "block/laser_back");
        return models().cube("laser", DEFAULT_BOTTOM, DEFAULT_TOP, laserTxt, laserBackTxt, laserBackTxt, laserBackTxt)
                .texture("particle", laserTxt);
    }
}
