package mcjty.deepresonance.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;

/**
 * Created by Elec332 on 9-7-2020
 */
public class DeepResonanceTags {

    public static final Tag<Block> RESONANT_ORE = tagBlock("resonant_ore");

    public static final Tag<Item> RESONANT_ORE_ITEM = tagItem("resonant_ore");

    private static Tag<Block> tagBlock(String name) {
        return new BlockTags.Wrapper(new DeepResonanceResourceLocation(name));
    }

    private static Tag<Item> tagItem(String name) {
        return new ItemTags.Wrapper(new DeepResonanceResourceLocation(name));
    }

}
