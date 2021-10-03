package mcjty.deepresonance.data;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.util.DeepResonanceTags;
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
        // @todo 1.16 fix me!
//        tag(DeepResonanceTags.RESONANT_ORE_ITEM)
//                .add(CoreModule.RESONATING_ORE_END_ITEM.get())
//                .add(CoreModule.RESONATING_ORE_NETHER_ITEM.get())
//                .add(CoreModule.RESONATING_ORE_STONE_ITEM.get());
    }

    @Override
    public String getName() {
        return "DeepResonance Tags";
    }
}
