package mcjty.deepresonance.datagen;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.util.DeepResonanceTags;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class ItemTags extends ItemTagsProvider {

    public ItemTags(DataGenerator generator, BlockTagsProvider blockTagsProvider, ExistingFileHelper helper) {
        super(generator, blockTagsProvider, DeepResonance.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(DeepResonanceTags.RESONANT_ORE_ITEM).add(CoreModule.RESONATING_ORE_STONE_ITEM.get(), CoreModule.RESONATING_ORE_NETHER_ITEM.get(), CoreModule.RESONATING_ORE_END_ITEM.get());
    }

    @Nonnull
    @Override
    public String getName() {
        return "DeepResonance Tags";
    }
}
