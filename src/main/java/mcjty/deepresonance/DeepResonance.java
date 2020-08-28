package mcjty.deepresonance;

import elec332.core.api.mod.IElecCoreMod;
import elec332.core.data.AbstractDataGenerator;
import elec332.core.util.FMLHelper;
import mcjty.deepresonance.data.DataGenerators;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.pulser.PulserModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.worldgen.WorldGenModule;
import mcjty.deepresonance.setup.Config;
import mcjty.deepresonance.setup.ModSetup;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.modules.Modules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 6-1-2020
 */
@Mod(DeepResonance.MODID)
public class DeepResonance implements IElecCoreMod {

    public static final String MODID = "deepresonance";
    public static final String MODNAME = FMLHelper.getModNameEarly(MODID);

    public static String SHIFT_MESSAGE = "message.rftoolsbase.shiftmessage";

    public static DeepResonance instance;
    public static ModSetup setup;
    public static Logger logger;

    private Modules modules = new Modules();

    public DeepResonance() {
        if (instance != null) {
            throw new RuntimeException();
        }
        instance = this;
        logger = LogManager.getLogger(MODNAME);
        setup = new ModSetup();

        setupModules();

        Config.register(modules);
        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(setup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AbstractDataGenerator.toEventListener(new DataGenerators()));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(modules::init);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(modules::initClient);
        });
    }

    private void setupModules() {
        modules.register(new CoreModule());
        modules.register(new GeneratorModule());
        modules.register(new MachinesModule());
        modules.register(new PulserModule());
        modules.register(new RadiationModule());
        modules.register(new TankModule());
        modules.register(new WorldGenModule());
    }

    @Override
    public void afterConstruction() {
        Config.afterRegister();
    }
}
