package mcjty.deepresonance.modules.worldgen;

import elec332.core.api.module.ElecModule;
import elec332.core.api.registration.APIInjectedEvent;
import elec332.core.api.world.IWorldGenManager;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import mcjty.deepresonance.modules.worldgen.world.DeepResonanceWorldGenRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.deepresonance.DeepResonance.MODID;

/**
 * Created by Elec332 on 9-7-2020
 */
@ElecModule(owner = MODID, name = "WorldGen")
public class WorldGenModule {

    private final DeepResonanceWorldGenRegistry worldGenRegistry;
    public static WorldGenConfiguration config;

    public WorldGenModule() {
        config = DeepResonance.configuration.registerConfig(WorldGenConfiguration::new, "worldgen", "WorldGen module settings");
        this.worldGenRegistry = new DeepResonanceWorldGenRegistry();
    }

    @ElecModule.EventHandler
    public void registerWorldGen(APIInjectedEvent<IWorldGenManager> event) {
        event.getInjectedAPI().registerWorldGenRegistry(worldGenRegistry, DeepResonance.instance);
    }

    @ElecModule.EventHandler
    public void commonSetup(FMLCommonSetupEvent event) {
        worldGenRegistry.doRegister = true;
    }

}
