package mcjty.deepresonance.radiation;

import com.google.common.collect.Sets;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Set;

public class RadiationShieldRegistry {
    private static Set<Block> leadBlocks = null;


    private static Set<Block> getLeadBlocks() {
        if (leadBlocks != null) {
            return leadBlocks;
        }
        leadBlocks = Sets.newHashSet();
        List<ItemStack> stacks = ItemStackTools.getOres("blockLead");
        for (ItemStack stack : stacks) {
            Item item = stack.getItem();
            if (item instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) item;
                if (itemBlock.getBlock() != null) {
                    leadBlocks.add(itemBlock.getBlock());
                }
            }
        }

        return leadBlocks;
    }


    public static float getBlocker(IBlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.OBSIDIAN) {
            return RadiationConfiguration.radiationShieldObsidianFactor;
        } else if (block == ModBlocks.denseObsidianBlock) {
            return RadiationConfiguration.radiationShieldDenseObsidianFactor;
        } else if (block == ModBlocks.denseGlassBlock) {
            return RadiationConfiguration.radiationShieldDenseGlassFactor;
        } else if (getLeadBlocks().contains(block)) {
            return RadiationConfiguration.radiationShieldLeadFactor;
        } else {
            return 1.0f;
        }
    }
}
