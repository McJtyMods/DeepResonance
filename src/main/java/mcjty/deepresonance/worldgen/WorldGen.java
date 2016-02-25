package mcjty.deepresonance.worldgen;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class WorldGen {

    public static void init() {
        DeepWorldGenerator generator = DeepWorldGenerator.instance;
        GameRegistry.registerWorldGenerator(generator, 5);
        MinecraftForge.EVENT_BUS.register(generator);
    }

}
