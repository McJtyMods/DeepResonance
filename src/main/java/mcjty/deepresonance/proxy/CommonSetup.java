package mcjty.deepresonance.proxy;

import elec332.core.handler.ElecCoreRegistrar;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.ForgeEventHandlers;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.generator.GeneratorConfiguration;
import mcjty.deepresonance.blocks.laser.LaserBonusConfiguration;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.deepresonance.integration.computers.OpenComputersIntegration;
import mcjty.deepresonance.items.ModItems;
import mcjty.deepresonance.network.DRMessages;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import mcjty.deepresonance.radiation.RadiationTickEvent;
import mcjty.deepresonance.radiation.SuperGenerationConfiguration;
import mcjty.deepresonance.tanks.TankGridHandler;
import mcjty.deepresonance.worldgen.WorldGen;
import mcjty.deepresonance.worldgen.WorldGenConfiguration;
import mcjty.deepresonance.worldgen.WorldTickHandler;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.network.PacketHandler;
import mcjty.lib.setup.DefaultCommonSetup;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.WrenchChecker;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Level;

import java.io.File;

public class CommonSetup extends DefaultCommonSetup {

    public boolean rftools = false;
    public boolean rftoolsControl = false;


    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        rftools = Loader.isModLoaded("rftools");
        rftoolsControl = Loader.isModLoaded("rftoolscontrol");

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());

        File modConfigDir = new File(getModConfigDir().getPath() + File.separator + "deepresonance");
        mainConfig = new Configuration(new File(modConfigDir, "main.cfg"));
        readMainConfig();

        SimpleNetworkWrapper network = PacketHandler.registerMessages(DeepResonance.MODID, "deepresonance");
        DRMessages.registerNetworkMessages(network);

        ModItems.init();
        ModBlocks.init();
        WorldGen.init();
        DRFluidRegistry.initFluids();

        ElecCoreRegistrar.GRIDHANDLERS.register(new TankGridHandler());
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();

        if (rftools) {
            Logging.log("Detected RFTools: enabling support");
            FMLInterModComms.sendFunctionMessage("rftools", "getScreenModuleRegistry", "mcjty.deepresonance.items.rftoolsmodule.RFToolsSupport$GetScreenModuleRegistry");
        }
        if (rftoolsControl) {
            Logging.log("Detected RFTools Control: enabling support");
            FMLInterModComms.sendFunctionMessage("rftoolscontrol", "getOpcodeRegistry", "mcjty.deepresonance.compat.rftoolscontrol.RFToolsControlSupport$GetOpcodeRegistry");
        }

        //@todo
//        FMLInterModComms.sendMessage("rftools", "dimlet_configure", "Material.tile.oreResonating=30000,6000,400,5");
    }

    @Override
    public void createTabs() {
        createTab("DeepResonance", new ItemStack(ModBlocks.resonatingCrystalBlock));
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();

            cfg.addCustomCategoryComment(WorldGenConfiguration.CATEGORY_WORLDGEN, "Configuration for worldgen");
            cfg.addCustomCategoryComment(GeneratorConfiguration.CATEGORY_GENERATOR, "Configuration for the generator multiblock");
            cfg.addCustomCategoryComment(RadiationConfiguration.CATEGORY_RADIATION, "Configuration for the radiation");
            cfg.addCustomCategoryComment(LaserBonusConfiguration.CATEGORY_LASERBONUS, "Configuration for the laser bonuses");
            cfg.addCustomCategoryComment(SuperGenerationConfiguration.CATEGORY_SUPERGEN, "Configuration for super power generation (using pulser)");
            WorldGenConfiguration.init(cfg);
            GeneratorConfiguration.init(cfg);
            RadiationConfiguration.init(cfg);
            LaserBonusConfiguration.init(cfg);
            SuperGenerationConfiguration.init(cfg);
        } catch (Exception e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        NetworkRegistry.INSTANCE.registerGuiHandler(DeepResonance.instance, new GuiProxy());
        MinecraftForge.EVENT_BUS.register(WorldTickHandler.instance);
        MinecraftForge.EVENT_BUS.register(new RadiationTickEvent());
        if (Loader.isModLoaded("opencomputers")) {
            OpenComputersIntegration.init();
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
        mainConfig = null;
        WrenchChecker.init();
    }
}
