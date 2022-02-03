package mcjty.deepresonance.modules.radiation.util;

import com.google.common.collect.Sets;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.Set;

public class RadiationShieldRegistry {
    private static Set<Block> leadBlocks = null;


    private static Set<Block> getLeadBlocks() {
        if (leadBlocks != null) {
            return leadBlocks;
        }
        // @todo 1.16
        leadBlocks = Sets.newHashSet();
//        List<ItemStack> stacks = OreDictionary.getOres("blockLead");
//        for (ItemStack stack : stacks) {
//            Item item = stack.getItem();
//            if (item instanceof ItemBlock) {
//                ItemBlock itemBlock = (ItemBlock) item;
//                if (itemBlock.getBlock() != null) {
//                    leadBlocks.add(itemBlock.getBlock());
//                }
//            }
//        }
//
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
