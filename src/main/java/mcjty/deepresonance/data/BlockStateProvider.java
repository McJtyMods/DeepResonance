package mcjty.deepresonance.data;

import elec332.core.data.AbstractBlockStateProvider;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.client.ModelLoaderCoreModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.client.TankRenderer;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;

/**
 * Created by Elec332 on 10-1-2020
 */
public class BlockStateProvider extends AbstractBlockStateProvider {

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

}
