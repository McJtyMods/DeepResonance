package mcjty.deepresonance.util;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.varia.TagTools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class DeepResonanceTags {

    public static final TagKey<Block> RESONANT_ORE = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "resonant_ore"));
    public static final TagKey<Item> RESONANT_ORE_ITEM = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "resonant_ore"));
    public static final TagKey<Block> STORAGE_BLOCKS_LEAD = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/lead"));

    public static void init() {

    }
}
