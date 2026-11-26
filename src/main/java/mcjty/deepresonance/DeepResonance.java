package mcjty.deepresonance;

import net.minecraft.resources.Identifier;
import java.util.Map;
import mcjty.rftoolsbase.tools.ClientItemDefinitions;
import mcjty.deepresonance.modules.radiation.client.RadiationOverlayRenderer;
import mcjty.deepresonance.modules.radiation.client.RadiationMonitorProperty;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.pedestal.PedestalModule;
import mcjty.deepresonance.modules.radiation.DataGenHelper;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.worldgen.WorldGenModule;
import mcjty.deepresonance.setup.Config;
import mcjty.deepresonance.setup.DeepResonanceMessages;
import mcjty.deepresonance.setup.ModSetup;
import mcjty.deepresonance.setup.Registration;
import mcjty.deepresonance.util.DeepResonanceTags;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import java.util.function.Function;

@Mod(DeepResonance.MODID)
public class DeepResonance {

    public static final String MODID = "deepresonance";

    public static final String SHIFT_MESSAGE = "message.deepresonance.shiftmessage";

    public static DeepResonance instance;
    public static ModSetup setup;

    private final Modules modules = new Modules();

    public DeepResonance(ModContainer mod, IEventBus bus, Dist dist) {
        instance = this;
        setup = new ModSetup();

        DeepResonanceTags.init();

        setupModules(bus, dist);

        Config.register(mod, bus, modules);
        Registration.register(bus);

        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(this::onDataGen);
        bus.addListener(setup.getBlockCapabilityRegistrar(Registration.RBLOCKS));
        bus.addListener(DeepResonanceMessages::registerMessages);

        if (dist.isClient()) {
            bus.addListener(modules::initClient);
            bus.addListener(RadiationMonitorProperty::register);
            bus.addListener((RegisterGuiLayersEvent event) -> event.registerAboveAll(
                    Identifier.fromNamespaceAndPath(MODID, "radiation"), RadiationOverlayRenderer::onRender));
        }
    }

    public static <T extends Item> Function<Item.Properties, T> tab(Function<Item.Properties, T> factory) {
        return properties -> setup.tab(() -> factory.apply(properties)).get();
    }

    private void onDataGen(GatherDataEvent.Client event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen, event.getWorldLookupProvider());
        datagen.generate();
        var generator = event.getGenerator();
        generator.addProvider(true, new ClientItemDefinitions(generator.getPackOutput(), MODID,
                Map.of("radiation_monitor", DataGenHelper.monitorDefinition())));
    }

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new CoreModule(bus, dist));
        modules.register(new GeneratorModule());
        modules.register(new MachinesModule(bus));
        modules.register(new RadiationModule());
        modules.register(new TankModule());
        modules.register(new WorldGenModule());
        modules.register(new PedestalModule(bus));
    }
}
