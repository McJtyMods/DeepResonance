package mcjty.deepresonance.worldgen;

import cpw.mods.fml.common.registry.GameRegistry;

public class WorldGen {
    public static void init() {
        GameRegistry.registerWorldGenerator(new DeepWorldGenerator(), 5);
    }
}
