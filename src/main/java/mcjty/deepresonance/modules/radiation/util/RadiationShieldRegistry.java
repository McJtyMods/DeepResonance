package mcjty.deepresonance.modules.radiation.util;

import com.google.common.collect.Sets;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.util.DeepResonanceTags;
import mcjty.lib.varia.TagTools;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class RadiationShieldRegistry {
    private static Set<Block> leadBlocks = null;


    private static Set<Block> getLeadBlocks() {
        if (leadBlocks != null) {
            return leadBlocks;
        }
        leadBlocks = Sets.newHashSet();
        Iterable<Holder<Block>> blocks = TagTools.getBlocksForTag(DeepResonanceTags.STORAGE_BLOCKS_LEAD);
        blocks.forEach(h -> leadBlocks.add(h.get()));
        return leadBlocks;
    }


    public static double getBlocker(BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.OBSIDIAN) {
            return RadiationConfiguration.RADIATION_SHIELD_OBSIDIAN_FACTOR.get();
        } else if (block == RadiationModule.DENSE_OBSIDIAN_BLOCK.get()) {
            return RadiationConfiguration.RADIATION_SHIELD_DENSE_OBSIDIAN_FACTOR.get();
        } else if (block == RadiationModule.DENSE_GLASS_BLOCK.get()) {
            return RadiationConfiguration.RADIATION_SHIELD_DENSE_GLASS_FACTOR.get();
        } else if (getLeadBlocks().contains(block)) {
            return RadiationConfiguration.RADIATION_SHIELD_LEAD_FACTOR.get();
        } else {
            return 1.0f;
        }
    }
}
