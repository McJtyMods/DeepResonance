package mcjty.deepresonance;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.worldgen.WorldGenModule;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.deepresonance.setup.Config;
import mcjty.deepresonance.setup.ModSetup;
import mcjty.deepresonance.setup.Registration;
import mcjty.deepresonance.util.DeepResonanceTags;
import mcjty.lib.modules.Modules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DeepResonance.MODID)
public class DeepResonance {

    public static final String MODID = "deepresonance";
    public static final String MODNAME = "DeepResonance";

    public static final String SHIFT_MESSAGE = "message.deepresonance.shiftmessage";

    public static DeepResonance instance;
    public static ModSetup setup;

    private final Modules modules = new Modules();

    public DeepResonance() {
        instance = this;
        setup = new ModSetup();

        DeepResonanceTags.init();

        setupModules();

        Config.register(modules);
        Registration.register();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(setup::init);
        bus.addListener(modules::init);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(ClientSetup::onTextureStitch);
            bus.addListener(modules::initClient);
        });
    }

    private void setupModules() {
        modules.register(new CoreModule());
        modules.register(new GeneratorModule());
        modules.register(new MachinesModule());
        modules.register(new RadiationModule());
        modules.register(new TankModule());
        modules.register(new WorldGenModule());
    }
}
