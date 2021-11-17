package mcjty.deepresonance.datagen;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.util.DeepResonanceTags;
import mcjty.rftoolsbase.RFToolsBase;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class BlockTags extends BlockTagsProvider {

    public BlockTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, RFToolsBase.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(DeepResonanceTags.RESONANT_ORE).add(CoreModule.RESONATING_ORE_STONE_BLOCK.get(), CoreModule.RESONATING_ORE_NETHER_BLOCK.get(), CoreModule.RESONATING_ORE_END_BLOCK.get());
    }

    @Override
    @Nonnull
    public String getName() {
        return "DeepResonance Tags";
    }
}
