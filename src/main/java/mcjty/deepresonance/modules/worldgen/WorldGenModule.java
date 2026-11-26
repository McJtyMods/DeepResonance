package mcjty.deepresonance.modules.worldgen;

import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import mcjty.deepresonance.modules.worldgen.world.ResonantCrystalFeature;
import mcjty.lib.modules.IModule;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.deepresonance.setup.Registration.FEATURES;

public class WorldGenModule implements IModule {

    static {
        FEATURES.register(ResonantCrystalFeature.FEATURE_CRYSTAL_ID.getPath(), () -> ResonantCrystalFeature.CODEC);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig(IEventBus bus) {
        WorldGenConfiguration.init();
    }
}
