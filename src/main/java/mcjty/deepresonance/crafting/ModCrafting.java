package mcjty.deepresonance.crafting;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModCrafting {
    public static void init() {
        GameRegistry.addSmelting(ModBlocks.resonatingOreBlock, new ItemStack(ModItems.resonatingPlateItem, 8), 0.0f);
    }
}
