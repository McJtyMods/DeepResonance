package mcjty.deepresonance.worldgen;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

public class WorldGen {
    public static void init() {
        DeepWorldGenerator generator = DeepWorldGenerator.instance;
        GameRegistry.registerWorldGenerator(generator, 5);
        MinecraftForge.EVENT_BUS.register(generator);
    }
}
