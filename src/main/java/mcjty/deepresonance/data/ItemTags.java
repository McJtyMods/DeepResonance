package mcjty.deepresonance.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;

public class ItemTags extends ItemTagsProvider {

    public ItemTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, new ForgeBlockTagsProvider(generator, helper));
    }

    @Override
    protected void addTags() {
        // @todo 1.16
//        copy(DeepResonanceTags.RESONANT_ORE, DeepResonanceTags.RESONANT_ORE_ITEM);
    }

    @Override
    public String getName() {
        return "DeepResonance Tags";
    }
}
