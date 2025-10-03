package mcjty.deepresonance.setup;

import mcjty.lib.modules.Modules;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public class Config {

    public static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec SERVER_CONFIG;
    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    public static void register(ModContainer mod, IEventBus bus, Modules modules) {
        modules.initConfig(bus);

        SERVER_CONFIG = SERVER_BUILDER.build();
        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();

        mod.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
        mod.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
        mod.registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);
    }
}
