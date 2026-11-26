package mcjty.deepresonance.modules.core;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import mcjty.lib.datagen.model.ConfiguredModel;
import mcjty.lib.datagen.model.ModelFile;

import java.util.function.Supplier;

public class DataGenHelper {

    public static void generateCrystal(Supplier<? extends Block> blockSupplier, BaseBlockStateProvider provider,
                                       String parentName, String parent, String texture) {
        Identifier crystal = Identifier.fromNamespaceAndPath(DeepResonance.MODID, parent);

        ModelFile emptyNatural = provider.models().withExistingParent(parentName, crystal).texture("crystal_texture", "deepresonance:block/" + texture)
                .texture("particle", "deepresonance:block/" + texture);
        provider.getVariantBuilder(blockSupplier.get()).forAllStates(state -> {
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return ConfiguredModel.builder()
                    .modelFile(emptyNatural)
                    .rotationY((int) ((direction.toYRot() + 180) % 360))
                    .build();
        });
    }

}
