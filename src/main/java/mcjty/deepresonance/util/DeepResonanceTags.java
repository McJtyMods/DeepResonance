package mcjty.deepresonance.util;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags;

public class DeepResonanceTags {

    public static final TagKey<Block> RESONANT_ORE = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(DeepResonance.MODID, "resonant_ore"));
    public static final TagKey<Item> RESONANT_ORE_ITEM = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(DeepResonance.MODID, "resonant_ore"));

    public static void init() {

    }
}
