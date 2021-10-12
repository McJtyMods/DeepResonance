package mcjty.deepresonance.modules.worldgen;

import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import mcjty.deepresonance.modules.worldgen.world.DeepResonanceWorldGenRegistry;
import mcjty.lib.modules.IModule;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class WorldGenModule implements IModule {

    private final DeepResonanceWorldGenRegistry worldGenRegistry;

    public WorldGenModule() {
        this.worldGenRegistry = new DeepResonanceWorldGenRegistry();

        // @todo 1.16
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IWorldGenManager.class, this::registerWorldGen);
    }

    // @todo 1.16
//    private void registerWorldGen(APIInjectedEvent<IWorldGenManager> event) {
//        event.getInjectedAPI().registerWorldGenRegistry(worldGenRegistry, DeepResonance.instance);
//    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        // @todo 1.16
//        worldGenRegistry.doRegister = true;
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {
        WorldGenConfiguration.init();
    }
}
