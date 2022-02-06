package mcjty.deepresonance.util;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags;

public class DeepResonanceTags {

    public static final Tags.IOptionalNamedTag<Block> RESONANT_ORE = BlockTags.createOptional(new ResourceLocation(DeepResonance.MODID, "resonant_ore"));
    public static final Tags.IOptionalNamedTag<Item> RESONANT_ORE_ITEM = ItemTags.createOptional(new ResourceLocation(DeepResonance.MODID, "resonant_ore"));

    public static void init() {

    }
}
