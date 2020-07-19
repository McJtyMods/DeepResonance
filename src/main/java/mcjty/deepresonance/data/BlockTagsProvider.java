package mcjty.deepresonance.data;

import elec332.core.data.AbstractBlockTagsProvider;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.util.DeepResonanceTags;
import net.minecraft.data.DataGenerator;

/**
 * Created by Elec332 on 9-7-2020
 */
public class BlockTagsProvider extends AbstractBlockTagsProvider {

    BlockTagsProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerBlockTags() {
        getBuilder(DeepResonanceTags.RESONANT_ORE).add(CoreModule.RESONATING_ORE_STONE_BLOCK.get(), CoreModule.RESONATING_ORE_NETHER_BLOCK.get(), CoreModule.RESONATING_ORE_END_BLOCK.get());
    }

}
