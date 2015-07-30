package mcjty.deepresonance.render;

import cpw.mods.fml.client.registry.ClientRegistry;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTESR;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;

public final class ModRenderers {

    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(ResonatingCrystalTileEntity.class, new ResonatingCrystalTESR());
    }
}
