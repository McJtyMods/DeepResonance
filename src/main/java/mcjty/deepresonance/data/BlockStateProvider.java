package mcjty.deepresonance.data;

import elec332.core.data.AbstractBlockStateProvider;
import elec332.core.util.BlockProperties;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.client.ModelLoaderCoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.pulser.PulserModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.client.TankRenderer;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

/**
 * Created by Elec332 on 10-1-2020
 */
public class BlockStateProvider extends AbstractBlockStateProvider {

    private static final ResourceLocation DEFAULT_TOP = new DeepResonanceResourceLocation("block/machine_top");
    private static final ResourceLocation DEFAULT_BOTTOM = new DeepResonanceResourceLocation("block/machine_bottom");
    private static final ResourceLocation DEFAULT_SIDE = new DeepResonanceResourceLocation("block/machine_side");

    BlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, DeepResonance.MODID, exFileHelper);
    }

    @Override
    protected void registerBlockStatesAndModels() {
        registerCrystalModel();

        simpleBlock(TankModule.TANK_BLOCK, models().cubeBottomTop("tank", TankRenderer.SIDE_TEXTURE, TankRenderer.BOTTOM_TEXTURE, TankRenderer.TOP_TEXTURE));
        simpleBlock(CoreModule.RESONATING_ORE_STONE_BLOCK);
        simpleBlock(CoreModule.RESONATING_ORE_NETHER_BLOCK);
        simpleBlock(CoreModule.RESONATING_ORE_END_BLOCK);
        simpleBlock(RadiationModule.POISONED_DIRT_BLOCK);
        simpleBlock(RadiationModule.DENSE_GLASS_BLOCK);
        simpleBlock(RadiationModule.DENSE_OBSIDIAN_BLOCK);
        simpleBlock(CoreModule.RESONATING_PLATE_BLOCK_BLOCK);
        simpleSide(MachinesModule.VALVE_BLOCK, new DeepResonanceResourceLocation("block/valve"));
        simpleFacingModel(MachinesModule.SMELTER_BLOCK, BlockProperties.ACTIVE);
        simpleFront(MachinesModule.PURIFIER_BLOCK);
        simpleFront(PulserModule.PULSER_BLOCK);
        simpleBlock(MachinesModule.LENS_BLOCK, models().withExistingParent("lens_mc", new DeepResonanceResourceLocation("lens")).texture("lens_texture", "deepresonance:block/lens"));
        simpleFacingModel(MachinesModule.LASER_BLOCK, createLaserModel());
        simpleFacingModel(MachinesModule.CRYSTALLIZER_BLOCK, createCrystallizerModel());
        simpleBlock(GeneratorModule.ENERGY_COLLECTOR_BLOCK, models().withExistingParent("energy_collector", new DeepResonanceResourceLocation("collector")).texture("collector_texture", "deepresonance:block/energy_collector"));
        simpleFacingModel(GeneratorModule.GENERATOR_CONTROLLER_BLOCK, BlockProperties.ON);
        simpleSide(GeneratorModule.GENERATOR_PART_BLOCK, state -> {
            String extra = "";
            if (state.get(BlockProperties.UP)) {
                extra += "_up";
            }
            if (state.get(BlockProperties.DOWN)) {
                extra += "_down";
            }
            if (state.get(BlockProperties.ON)) {
                extra += "_on";
            }
            return new DeepResonanceResourceLocation("block/generator_part_side" + extra);
        });
    }

    private void registerCrystalModel() {
        ModelFile empty = models().withExistingParent("crystal_empty", new DeepResonanceResourceLocation("crystal")).texture("crystal_texture", "deepresonance:block/empty_crystal");
        ModelFile empty_pure = models().withExistingParent("crystal_empty_pure", new DeepResonanceResourceLocation("crystal_generated")).texture("crystal_texture", "deepresonance:block/empty_crystal");
        ModelFile full = models().withExistingParent("crystal_full", new DeepResonanceResourceLocation("crystal")).texture("crystal_texture", "deepresonance:block/crystal");
        ModelFile full_pure = models().withExistingParent("crystal_full_pure", new DeepResonanceResourceLocation("crystal_generated")).texture("crystal_texture", "deepresonance:block/crystal");

        simpleFacingModel(ModelLoaderCoreModule.stateContainer.getOwner(), state -> {
            if (state.get(ModelLoaderCoreModule.EMPTY)) {
                if (state.get(ModelLoaderCoreModule.VERY_PURE)) {
                    return empty_pure;
                } else {
                    return empty;
                }
            } else {
                if (state.get(ModelLoaderCoreModule.VERY_PURE)) {
                    return full_pure;
                } else {
                    return full;
                }
            }
        });
    }

    public BlockModelBuilder createCrystallizerModel() {
        BlockModelBuilder ret = simpleFront("crystallizer", new DeepResonanceResourceLocation("block/crystallizer"));
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
        BlockModelBuilder ret = simpleFront("laser", new DeepResonanceResourceLocation("block/laser"), new DeepResonanceResourceLocation("block/laser_back"));
        ret.texture("back", new DeepResonanceResourceLocation("block/laser_back_color"));
        ModelBuilder<BlockModelBuilder>.ElementBuilder elem = ret.element();
        for (Direction direction : Direction.values()) {
            elem = elem.face(direction).cullface(direction).texture("#" + direction.getName()).end();
        }
        ret = elem.end();
        return ret.element().from(0, 0, 1).to(16, 16, 16).face(Direction.SOUTH).cullface(Direction.SOUTH).texture("#back").tintindex(1).end().end();
    }

    @Override
    protected ResourceLocation getDefaultTopLocation() {
        return DEFAULT_TOP;
    }

    @Override
    protected ResourceLocation getDefaultBottomLocation() {
        return DEFAULT_BOTTOM;
    }

    @Override
    protected ResourceLocation getDefaultSideLocation() {
        return DEFAULT_SIDE;
    }

}
