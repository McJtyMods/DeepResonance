package mcjty.deepresonance.varia;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class BlockInfo {

    public static String getReadableName(IBlockState state) {
        return getReadableName(state.getBlock(), state.getBlock().getMetaFromState(state));
    }

    public static String getReadableName(Block block, BlockPos coordinate, int metadata, World world) {
        List<ItemStack> itemStacks = block.getDrops(world, coordinate, world.getBlockState(coordinate), 1);
        if (itemStacks != null && !itemStacks.isEmpty() && itemStacks.get(0).getItem() != null) {
            return getReadableName(itemStacks.get(0).getItem(), metadata);
        }

        return getReadableName(block, metadata);
    }

    public static String getReadableName(Object object, int metadata) {
        if (object instanceof Block) {
            return getReadableName((Block) object, metadata);
        } else if (object instanceof Item) {
            return getReadableName((Item) object, metadata);
        } else if (object instanceof ItemStack) {
            ItemStack s = (ItemStack) object;
            return s.getDisplayName();
        } else {
            return "?";
        }
    }

    public static String getReadableName(Block block, int metadata) {
        ItemStack s = new ItemStack(block, 1, metadata);
        String displayName;
        if (s.getItem() == null) {
            return block.getUnlocalizedName();
        } else {
            displayName = s.getDisplayName();
        }
        if (displayName.startsWith("tile.")) {
            displayName = displayName.substring(5);
        }
        if (displayName.endsWith(".name")) {
            displayName = displayName.substring(0, displayName.length()-5);
        }
        return displayName;
    }

    private static String getReadableName(Item item, int metadata) {
        ItemStack s = new ItemStack(item, 1, metadata);
        String displayName = s.getDisplayName();
        if (displayName.startsWith("tile.")) {
            displayName = displayName.substring(5);
        }
        if (displayName.endsWith(".name")) {
            displayName = displayName.substring(0, displayName.length()-5);
        }
        return displayName;
    }
}
