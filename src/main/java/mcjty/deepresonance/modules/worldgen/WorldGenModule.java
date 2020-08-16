package mcjty.deepresonance.modules.worldgen;

import elec332.core.api.registration.APIInjectedEvent;
import elec332.core.api.world.IWorldGenManager;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import mcjty.deepresonance.modules.worldgen.world.DeepResonanceWorldGenRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Created by Elec332 on 9-7-2020
 */
public class WorldGenModule {

    private final DeepResonanceWorldGenRegistry worldGenRegistry;
    public static WorldGenConfiguration config;

    public WorldGenModule(IEventBus eventBus) {
        config = DeepResonance.configuration.registerConfig(WorldGenConfiguration::new, "worldgen", "WorldGen module settings");
        this.worldGenRegistry = new DeepResonanceWorldGenRegistry();

        eventBus.addListener(this::setup);
        eventBus.addGenericListener(IWorldGenManager.class, this::registerWorldGen);
    }

    private void setup(FMLCommonSetupEvent event) {
        worldGenRegistry.doRegister = true;
    }

    private void registerWorldGen(APIInjectedEvent<IWorldGenManager> event) {
        event.getInjectedAPI().registerWorldGenRegistry(worldGenRegistry, DeepResonance.instance);
    }

}
