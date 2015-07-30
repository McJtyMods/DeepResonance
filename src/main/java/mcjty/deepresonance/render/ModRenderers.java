package mcjty.deepresonance.render;

import cpw.mods.fml.client.registry.ClientRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalItemRenderer;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTESR;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public final class ModRenderers {

    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(ResonatingCrystalTileEntity.class, new ResonatingCrystalTESR());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.resonatingCrystalBlock), new ResonatingCrystalItemRenderer());
    }
}
