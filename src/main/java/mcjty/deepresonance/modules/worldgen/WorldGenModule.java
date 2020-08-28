package mcjty.deepresonance.modules.worldgen;

import elec332.core.api.registration.APIInjectedEvent;
import elec332.core.api.world.IWorldGenManager;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import mcjty.deepresonance.modules.worldgen.world.DeepResonanceWorldGenRegistry;
import mcjty.deepresonance.setup.Config;
import mcjty.lib.modules.IModule;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Created by Elec332 on 9-7-2020
 */
public class WorldGenModule implements IModule {

    private final DeepResonanceWorldGenRegistry worldGenRegistry;
    public static WorldGenConfiguration config;

    public WorldGenModule() {
        this.worldGenRegistry = new DeepResonanceWorldGenRegistry();

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IWorldGenManager.class, this::registerWorldGen);
    }

    private void registerWorldGen(APIInjectedEvent<IWorldGenManager> event) {
        event.getInjectedAPI().registerWorldGenRegistry(worldGenRegistry, DeepResonance.instance);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        worldGenRegistry.doRegister = true;
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {
        config = Config.configuration.registerConfig(WorldGenConfiguration::new, "worldgen", "WorldGen module settings");
    }
}
