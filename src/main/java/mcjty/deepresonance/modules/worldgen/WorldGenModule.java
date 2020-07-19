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

    public WorldGenModule() {
        WorldGenConfiguration configuration = new WorldGenConfiguration();
        DeepResonance.config.configureSubConfig("worldgen", "WorldGen module settings", config -> {
            config.registerConfigurableElement(configuration);
        });
        this.worldGenRegistry = new DeepResonanceWorldGenRegistry(configuration);
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
