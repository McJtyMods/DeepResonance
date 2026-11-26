package mcjty.deepresonance.setup;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import static mcjty.deepresonance.DeepResonance.MODID;
import mcjty.lib.modules.Modules;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.config.ModConfig;

public class Config {

    public static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec SERVER_CONFIG;
    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    public static void register(Modules modules) {
        modules.initConfig();

        SERVER_CONFIG = SERVER_BUILDER.build();
        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();

        ConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.CLIENT, CLIENT_CONFIG);
        ConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.COMMON, COMMON_CONFIG);
        ConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.SERVER, SERVER_CONFIG);
    }
}
