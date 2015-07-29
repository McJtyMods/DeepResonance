package mcjty.deepresonance.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.deepresonance.blocks.ore.ResonatingOreBlock;

public final class ModBlocks {
    public static ResonatingOreBlock resonatingOreBlock;

    public static void init() {
        resonatingOreBlock = new ResonatingOreBlock();
        GameRegistry.registerBlock(resonatingOreBlock, "resonatingOreBlock");
    }
}
