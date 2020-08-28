package mcjty.deepresonance.setup;

import elec332.core.api.config.IConfigWrapper;
import elec332.core.config.ConfigWrapper;
import elec332.core.util.FMLHelper;
import mcjty.lib.modules.Modules;
import net.minecraftforge.fml.config.ModConfig;

public class Config {

    public static IConfigWrapper configuration;
    public static IConfigWrapper clientConfiguration;

    public static void register(Modules modules) {
        configuration = new ConfigWrapper(FMLHelper.getActiveModContainer());
        clientConfiguration = new ConfigWrapper(FMLHelper.getActiveModContainer(), ModConfig.Type.CLIENT);

        modules.initConfig();
    }

    public static void afterRegister() {
        configuration.register();
        clientConfiguration.register();
    }
}
