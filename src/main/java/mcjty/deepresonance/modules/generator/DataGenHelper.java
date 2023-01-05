package mcjty.deepresonance.modules.generator;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Supplier;

import static mcjty.deepresonance.datagen.BlockStates.DEFAULT_BOTTOM;
import static mcjty.deepresonance.datagen.BlockStates.DEFAULT_TOP;

public class DataGenHelper {

    public static void registerGeneratorPart(Supplier<? extends Block> blockSupplier, BaseBlockStateProvider provider) {
        provider.variantBlock(blockSupplier.get(), state -> {
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
            return provider.models().cube("generator_part" + extra, DEFAULT_BOTTOM, DEFAULT_TOP, id, id, id, id)
                    .texture("particle", id);
        });
    }


}
