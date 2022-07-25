package mcjty.deepresonance.modules.worldgen;

import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import mcjty.deepresonance.modules.worldgen.world.DeepResonanceOreGenerator;
import mcjty.deepresonance.modules.worldgen.world.ResonantCrystalFeature;
import mcjty.deepresonance.modules.worldgen.world.ResonantCrystalFeatureConfig;
import mcjty.lib.modules.IModule;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.deepresonance.setup.Registration.FEATURES;

public class WorldGenModule implements IModule {

    public static final RegistryObject<ResonantCrystalFeature> CRYSTAL_FEATURE = FEATURES.register(
            ResonantCrystalFeature.FEATURE_CRYSTAL_ID.getPath(),
            () -> new ResonantCrystalFeature(ResonantCrystalFeatureConfig.CODEC));


    @Override
    public void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DeepResonanceOreGenerator.registerConfiguredFeatures();
            ResonantCrystalFeature.registerConfiguredFeatures();
        });
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {
        WorldGenConfiguration.init();
    }
}
