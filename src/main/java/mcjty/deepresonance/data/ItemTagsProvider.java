package mcjty.deepresonance.data;

import elec332.core.data.AbstractItemTagsProvider;
import mcjty.deepresonance.util.DeepResonanceTags;
import net.minecraft.data.DataGenerator;

/**
 * Created by Elec332 on 9-7-2020
 */
public class ItemTagsProvider extends AbstractItemTagsProvider {

    ItemTagsProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerItemTags() {
        copy(DeepResonanceTags.RESONANT_ORE, DeepResonanceTags.RESONANT_ORE_ITEM);
    }

}
