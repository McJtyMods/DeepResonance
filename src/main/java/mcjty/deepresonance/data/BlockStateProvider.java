package mcjty.deepresonance.data;

import com.google.common.base.Preconditions;
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
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 10-1-2020
 */
public class BlockStateProvider extends AbstractBlockStateProvider {

    private static final ResourceLocation DEFAULT_TOP = new DeepResonanceResourceLocation("block/machine_top");
    private static final ResourceLocation DEFAULT_BOTTOM = new DeepResonanceResourceLocation("block/machine_bottom");

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

        registerCrystalModel();
    }

    private void registerCrystalModel() {
        ModelFile empty = models().withExistingParent("crystal_empty", new DeepResonanceResourceLocation("crystal")).texture("crystal_texture", "deepresonance:block/empty_crystal");
        ModelFile empty_pure = models().withExistingParent("crystal_empty_pure", new DeepResonanceResourceLocation("crystal_generated")).texture("crystal_texture", "deepresonance:block/empty_crystal");
        ModelFile full = models().withExistingParent("crystal_full", new DeepResonanceResourceLocation("crystal")).texture("crystal_texture", "deepresonance:block/crystal");
        ModelFile full_pure = models().withExistingParent("crystal_full_pure", new DeepResonanceResourceLocation("crystal_generated")).texture("crystal_texture", "deepresonance:block/crystal");

        getVariantBuilder(ModelLoaderCoreModule.stateContainer.getOwner()).forAllStates(state -> {
            ModelFile file;
            if (state.get(ModelLoaderCoreModule.EMPTY)) {
                if (state.get(ModelLoaderCoreModule.VERY_PURE)) {
                    file = empty_pure;
                } else {
                    file = empty;
                }
            } else {
                if (state.get(ModelLoaderCoreModule.VERY_PURE)) {
                    file = full_pure;
                } else {
                    file = full;
                }
            }
            return ConfiguredModel.builder()
                    .modelFile(file)
                    .rotationY(((int) state.get(ModelLoaderCoreModule.FACING).getHorizontalAngle() + 180) % 360)
                    .build();
        });
    }

    private void simpleSide(Supplier<Block> blockSupplier, ResourceLocation sides) {
        Block block = blockSupplier.get();
        simpleBlock(block, models().cubeBottomTop(Preconditions.checkNotNull(block.getRegistryName()).getPath(), sides, BlockStateProvider.DEFAULT_BOTTOM, BlockStateProvider.DEFAULT_TOP));
    }

}
