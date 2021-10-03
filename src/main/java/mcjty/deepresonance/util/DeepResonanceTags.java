package mcjty.deepresonance.util;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

public class DeepResonanceTags {


    public static final ITag.INamedTag<Block> RESONANT_ORE = BlockTags.bind(DeepResonance.MODID+":resonant_ore");
    public static final ITag.INamedTag<Item> RESONANT_ORE_ITEM = ItemTags.bind(DeepResonance.MODID+":resonant_ore");

//    private static Tag<Block> tagBlock(String name) {
//        return new BlockTags.Wrapper(new ResourceLocation(DeepResonance.MODID, name));
//    }
//
//    private static Tag<Item> tagItem(String name) {
//        return new ItemTags.Wrapper(new ResourceLocation(DeepResonance.MODID, name));
//    }

}
