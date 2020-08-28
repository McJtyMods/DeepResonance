package mcjty.deepresonance;

import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.module.IModuleController;
import elec332.core.api.module.IModuleInfo;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 6-1-2020
 */
@Mod(DeepResonance.MODID)
public class DeepResonance implements IElecCoreMod, IModuleController {

    public static final String MODID = "deepresonance";
    public static final String MODNAME = FMLHelper.getModNameEarly(MODID);

    public static String SHIFT_MESSAGE = "message.rftoolsbase.shiftmessage";

    public static DeepResonance instance;
    public static ModSetup setup;
    public static Logger logger;

    public DeepResonance() {
        if (instance != null) {
            throw new RuntimeException();
        }
        instance = this;
        logger = LogManager.getLogger(MODNAME);
        setup = new ModSetup();

        Config.register();
        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(setup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AbstractDataGenerator.toEventListener(new DataGenerators()));

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(CoreModule::initClient);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(GeneratorModule::initClient);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(MachinesModule::initClient);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(TankModule::initClient);
        });
    }

    @Override
    public void afterConstruction() {
        Config.afterRegister();
    }

    @Override
    public boolean isModuleEnabled(String moduleName) {
        return true;
    }

    @Override
    public void registerAdditionalModules(Consumer<IModuleInfo> registry, BiFunction<String, Class<?>, IModuleInfo.Builder> factory1, BiFunction<String, String, IModuleInfo.Builder> factory2) {
        registry.accept(factory1.apply("Core", CoreModule.class).alwaysEnabled().build());
        registry.accept(factory1.apply("Generator", GeneratorModule.class).build());
        registry.accept(factory1.apply("Machines", MachinesModule.class).build());
        registry.accept(factory1.apply("Pulser", PulserModule.class).build());
        registry.accept(factory1.apply("Radiation", RadiationModule.class).build());
        registry.accept(factory1.apply("Tanks", TankModule.class).build());
        registry.accept(factory1.apply("WorldGen", WorldGenModule.class).build());
    }

    @Override
    public ForgeConfigSpec.BooleanValue getModuleConfig(String moduleName) {
        return Config.configuration.registerConfig(builder -> builder.comment("Whether the " + moduleName.toLowerCase() + " should be enabled").define(moduleName.toLowerCase() + ".enabled", true));
    }

}
