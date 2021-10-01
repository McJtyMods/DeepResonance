package mcjty.deepresonance.setup;

import mcjty.lib.modules.Modules;

public class Config {

    // @todo 1.16
//    public static IConfigWrapper configuration;
//    public static IConfigWrapper clientConfiguration;

    public static void register(Modules modules) {
//        configuration = new ConfigWrapper(FMLHelper.getActiveModContainer());
//        clientConfiguration = new ConfigWrapper(FMLHelper.getActiveModContainer(), ModConfig.Type.CLIENT);

        modules.initConfig();
    }

    public static void afterRegister() {
//        configuration.register();
//        clientConfiguration.register();
    }
}
