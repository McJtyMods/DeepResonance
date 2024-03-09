package mcjty.deepresonance.modules.worldgen;

import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import mcjty.deepresonance.modules.worldgen.world.ResonantCrystalFeature;
import mcjty.deepresonance.modules.worldgen.world.ResonantCrystalFeatureConfig;
import mcjty.lib.modules.IModule;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.deepresonance.setup.Registration.FEATURES;

public class WorldGenModule implements IModule {

    public static final Supplier<ResonantCrystalFeature> CRYSTAL_FEATURE = FEATURES.register(
            ResonantCrystalFeature.FEATURE_CRYSTAL_ID.getPath(),
            () -> new ResonantCrystalFeature(ResonantCrystalFeatureConfig.CODEC));

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
