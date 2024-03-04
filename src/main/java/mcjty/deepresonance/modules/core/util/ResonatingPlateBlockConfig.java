package mcjty.deepresonance.modules.core.util;

import mcjty.deepresonance.setup.Config;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fml.config.ModConfig;
import net.neoforged.neoforge.fml.event.config.ModConfigEvent;

public class ResonatingPlateBlockConfig {

    private static ModConfigSpec.IntValue RADIATION_STRENGTH;
    private static int radiationStrength;

    public static ModConfigSpec.IntValue RADIATION_RADIUS;
    public static ModConfigSpec.IntValue RADIATION_TICKS;

    public static void init(IEventBus bus) {
        bus.addListener(ResonatingPlateBlockConfig::onConfigReload);

        Config.SERVER_BUILDER.push("plate");

        RADIATION_STRENGTH = Config.SERVER_BUILDER.comment("Strength of radiation that a plate block gives when it has a redstone signal. 0 to disable")
                .defineInRange("radiationStrength", 20000, 0, 100000);
        RADIATION_RADIUS = Config.SERVER_BUILDER.comment("Radius of radiation that a plate block gives when it has a redstone signal")
                .defineInRange("radiationRadius", 10, 8, 128);
        RADIATION_TICKS = Config.SERVER_BUILDER.comment("Amount of ticks that the radiation from a plate block lasts")
                .defineInRange("radiationTicks", 100, 20, 72000);

        Config.SERVER_BUILDER.pop();
    }

    public static int getRadiationStrength() {
        return radiationStrength;
    }

    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            radiationStrength = RADIATION_STRENGTH.get();
        }
    }
}
