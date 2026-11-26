package mcjty.deepresonance;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.pedestal.PedestalModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.worldgen.WorldGenModule;
import mcjty.deepresonance.setup.Config;
import mcjty.deepresonance.setup.DeepResonanceMessages;
import mcjty.deepresonance.setup.ModSetup;
import mcjty.deepresonance.setup.Registration;
import mcjty.deepresonance.util.DeepResonanceTags;
import mcjty.lib.modules.Modules;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class DeepResonance implements ModInitializer {

    public static final String MODID = "deepresonance";
    public static final String SHIFT_MESSAGE = "message.deepresonance.shiftmessage";

    public static final ModSetup setup = new ModSetup();
    public static final Modules modules = new Modules();

    @Override
    public void onInitialize() {
        DeepResonanceTags.init();
        modules.register(new CoreModule());
        modules.register(new GeneratorModule());
        modules.register(new MachinesModule());
        modules.register(new RadiationModule());
        modules.register(new TankModule());
        modules.register(new WorldGenModule());
        modules.register(new PedestalModule());

        Config.register(modules);
        Registration.register();
        DeepResonanceMessages.registerMessages();
        setup.init();
        modules.init();
        setup.registerBlockCapabilities(Registration.RBLOCKS);
    }

    public static <T extends Item> Function<Item.Properties, T> tab(Function<Item.Properties, T> factory) {
        return properties -> setup.tab(() -> factory.apply(properties)).get();
    }
}
