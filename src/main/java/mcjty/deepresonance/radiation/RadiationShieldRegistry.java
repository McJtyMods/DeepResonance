package mcjty.deepresonance.radiation;

import mcjty.deepresonance.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RadiationShieldRegistry {
    private static Set<Block> leadBlocks = null;


    private static Set<Block> getLeadBlocks() {
        if (leadBlocks != null) {
            return leadBlocks;
        }
        leadBlocks = new HashSet<Block>();
        ArrayList<ItemStack> stacks = OreDictionary.getOres("blockLead");
        for (ItemStack stack : stacks) {
            Item item = stack.getItem();
            if (item instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) item;
                if (itemBlock.field_150939_a != null) {
                    leadBlocks.add(itemBlock.field_150939_a);
                }
            }
        }

        return leadBlocks;
    }


    public static float getBlocker(Block block) {
        if (block == Blocks.obsidian) {
            return RadiationConfiguration.radiationShieldObsidianFactor;
        } else if (block == ModBlocks.denseObsidianBlock) {
            return RadiationConfiguration.radiationShieldDenseObsidianFactor;
        } else if (getLeadBlocks().contains(block)) {
            return RadiationConfiguration.radiationShieldLeadFactor;
        } else {
            return 1.0f;
        }
    }
}
