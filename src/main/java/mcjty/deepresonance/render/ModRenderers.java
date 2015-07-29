package mcjty.deepresonance.render;

import cpw.mods.fml.client.registry.RenderingRegistry;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalBlock;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalRenderer;

public final class ModRenderers {

    public static void init() {
        ResonatingCrystalBlock.RENDERID_RESONATINGCRYSTAL = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(ResonatingCrystalBlock.RENDERID_RESONATINGCRYSTAL, new ResonatingCrystalRenderer());
    }
}
