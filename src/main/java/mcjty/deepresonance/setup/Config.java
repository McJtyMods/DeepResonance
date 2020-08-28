package mcjty.deepresonance.setup;

import elec332.core.api.config.IConfigWrapper;
import elec332.core.config.ConfigWrapper;
import elec332.core.util.FMLHelper;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.pulser.PulserModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.worldgen.WorldGenModule;
import net.minecraftforge.fml.config.ModConfig;

public class Config {

    public static IConfigWrapper configuration;
    public static IConfigWrapper clientConfiguration;

    public static void register() {
        configuration = new ConfigWrapper(FMLHelper.getActiveModContainer());
        clientConfiguration = new ConfigWrapper(FMLHelper.getActiveModContainer(), ModConfig.Type.CLIENT);

        CoreModule.setupConfig(configuration);
        GeneratorModule.setupConfig(configuration);
        MachinesModule.setupConfig(configuration);
        PulserModule.setupConfig(configuration);
        RadiationModule.setupConfig(configuration);
        WorldGenModule.setupConfig(configuration);
        TankModule.setupConfig(configuration, clientConfiguration);
    }

    public static void afterRegister() {
        configuration.register();
        clientConfiguration.register();
    }
}
