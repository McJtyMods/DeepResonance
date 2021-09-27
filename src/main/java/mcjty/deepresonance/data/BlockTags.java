package mcjty.deepresonance.data;

import mcjty.rftoolsbase.RFToolsBase;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTags extends BlockTagsProvider {

    public BlockTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, RFToolsBase.MODID, helper);
    }

    @Override
    protected void addTags() {
        // @todo 1.16
//        getBuilder(DeepResonanceTags.RESONANT_ORE).add(CoreModule.RESONATING_ORE_STONE_BLOCK.get(), CoreModule.RESONATING_ORE_NETHER_BLOCK.get(), CoreModule.RESONATING_ORE_END_BLOCK.get());
    }

    @Override
    public String getName() {
        return "DeepResonance Tags";
    }
}
