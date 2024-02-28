package mcjty.deepresonance.modules.core;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import java.util.function.Supplier;

public class DataGenHelper {

    public static void generateCrystal(Supplier<? extends Block> blockSupplier, BaseBlockStateProvider provider,
                                       String parentName, String parent, String texture) {
        ResourceLocation crystal = new ResourceLocation(DeepResonance.MODID, parent);

        ModelFile emptyNatural = provider.models().withExistingParent(parentName, crystal).texture("crystal_texture", "deepresonance:block/" + texture)
                .texture("particle", "deepresonance:block/" + texture).renderType("translucent");
        provider.getVariantBuilder(blockSupplier.get()).forAllStates(state -> {
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return ConfiguredModel.builder()
                    .modelFile(emptyNatural)
                    .rotationY((int) ((direction.toYRot() + 180) % 360))
                    .build();
        });
    }


}
