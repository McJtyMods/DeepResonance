package mcjty.deepresonance.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.data.AbstractBlockStateProvider;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.client.ModelLoaderCoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.client.TankRenderer;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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
        simpleBlock(TankModule.TANK_BLOCK.get(), models().cubeBottomTop("tank", TankRenderer.SIDE_TEXTURE, TankRenderer.BOTTOM_TEXTURE, TankRenderer.TOP_TEXTURE));
        simpleBlock(CoreModule.RESONATING_ORE_STONE_BLOCK);
        simpleBlock(CoreModule.RESONATING_ORE_NETHER_BLOCK);
        simpleBlock(CoreModule.RESONATING_ORE_END_BLOCK);
        simpleBlock(RadiationModule.POISONED_DIRT_BLOCK);
        simpleBlock(RadiationModule.DENSE_GLASS_BLOCK);
        simpleBlock(RadiationModule.DENSE_OBSIDIAN_BLOCK);
        simpleBlock(CoreModule.RESONATING_PLATE_BLOCK_BLOCK);
        simpleSide(MachinesModule.VALVE_BLOCK, new DeepResonanceResourceLocation("block/valve"));
        simpleFacingModel(MachinesModule.SMELTER_BLOCK, (state, generator) -> state.get(BlockStateProperties.LIT) ? generator.apply(new DeepResonanceResourceLocation("block/smelter_active")) : generator.apply(new DeepResonanceResourceLocation("block/smelter_inactive")));
        simpleFront(MachinesModule.PURIFIER_BLOCK);
        simpleFront(MachinesModule.PULSER_BLOCK);

        registerCrystalModel();
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

    private void simpleSide(Supplier<Block> blockSupplier, ResourceLocation sides) {
        Block block = blockSupplier.get();
        simpleBlock(block, models().cubeBottomTop(Preconditions.checkNotNull(block.getRegistryName()).getPath(), sides, BlockStateProvider.DEFAULT_BOTTOM, BlockStateProvider.DEFAULT_TOP));
    }

    private void simpleFacingModel(Supplier<Block> blockSupplier, BiFunction<BlockState, Function<ResourceLocation, ModelFile>, ModelFile> front) {
        simpleFacingModel(blockSupplier.get(), front);
    }

    private void simpleFront(Supplier<Block> blockSupplier) {
        Block block = blockSupplier.get();
        simpleFront(block, blockTexture(block));
    }

    private void simpleFront(Block block, ResourceLocation front) {
        ModelFile model = simpleFront(Preconditions.checkNotNull(block.getRegistryName()).getPath(), front);
        simpleFacingModel(block, state -> model);
    }

    private void simpleFacingModel(Supplier<Block> blockSupplier, Function<BlockState, ModelFile> front) {
        simpleFacingModel(blockSupplier.get(), front);
    }

    private void simpleFacingModel(Block block, BiFunction<BlockState, Function<ResourceLocation, ModelFile>, ModelFile> front) {
        Map<String, ModelFile> map = Maps.newHashMap();
        simpleFacingModel(block, state -> front.apply(state, rl -> {
            String path = rl.getPath();
            int idx = path.lastIndexOf("/");
            if (idx >= 0) {
                path = path.substring(idx + 1);
            }
            return map.computeIfAbsent(path, name -> simpleFront(name, rl));
        }));
    }

    private void simpleFacingModel(Block block, Function<BlockState, ModelFile> front) {
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(front.apply(state))
                .rotationY(((int) state.get(ModelLoaderCoreModule.FACING).getHorizontalAngle() + 180) % 360)
                .build());
    }

    private BlockModelBuilder simpleFront(String name, ResourceLocation front) {
        return models().cube(Preconditions.checkNotNull(name), BlockStateProvider.DEFAULT_BOTTOM, BlockStateProvider.DEFAULT_TOP, front, DEFAULT_SIDE, DEFAULT_SIDE, DEFAULT_SIDE);
    }

}
