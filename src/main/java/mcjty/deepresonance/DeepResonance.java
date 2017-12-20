package mcjty.deepresonance;

import elec332.core.api.network.INetworkHandler;
import elec332.core.api.network.ModNetworkHandler;
import elec332.core.config.ConfigWrapper;
import elec332.core.main.ElecCoreRegistrar;
import elec332.core.util.LoadTimer;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.commands.CommandDRGen;
import mcjty.deepresonance.compat.CompatHandler;
import mcjty.deepresonance.compat.handlers.ComputerCraftCompatHandler;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.deepresonance.integration.computers.OpenComputersIntegration;
import mcjty.deepresonance.items.manual.GuiDeepResonanceManual;
import mcjty.deepresonance.proxy.CommonProxy;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.tanks.TankGridHandler;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.CompatCreativeTabs;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.varia.Logging;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = DeepResonance.MODID, name="DeepResonance",
        dependencies =
                        "required-after:mcjtylib_ng@[" + DeepResonance.MIN_MCJTYLIB_VER + ",);" +
                        "required-after:compatlayer@[" + DeepResonance.COMPATLAYER_VER + ",);" +
                        "required-after:eleccore@[" + DeepResonance.MIN_ELECCORE_VER + ",);" +
                        "after:Forge@[" + DeepResonance.MIN_FORGE10_VER + ",);" +
                        "after:forge@[" + DeepResonance.MIN_FORGE11_VER + ",);" +
                        "after:opencomputers@[" + DeepResonance.MIN_OPENCOMPUTERS_VER + ",);" +
                        "after:OpenComputers@[" + DeepResonance.MIN_OPENCOMPUTERS_VER + ",)",
        version = DeepResonance.VERSION,
        acceptedMinecraftVersions = "[1.10,1.12)")
public class DeepResonance implements ModBase {
    public static final String MODID = "deepresonance";
    public static final String VERSION = "1.4.9";
    public static final String MIN_ELECCORE_VER = "1.6.345";
    public static final String MIN_OPENCOMPUTERS_VER = "1.7.0.146";
    public static final String MIN_FORGE10_VER = "12.18.1.2082";
    public static final String MIN_FORGE11_VER = "13.19.0.2176";
    public static final String MIN_MCJTYLIB_VER = "2.5.1";
    public static final String COMPATLAYER_VER = "0.3.0";

    @SidedProxy(clientSide="mcjty.deepresonance.proxy.ClientProxy", serverSide="mcjty.deepresonance.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance("deepresonance")
    public static DeepResonance instance;
    public static Logger logger;
    public static File mainConfigDir;
    public static File modConfigDir;
    public static Configuration config;
    public static Configuration versionConfig;
    public static CompatHandler compatHandler;
    public static ConfigWrapper configWrapper;
    @ModNetworkHandler
    public static INetworkHandler networkHandler;
    private static LoadTimer loadTimer;

    public boolean rftools = false;
    public boolean rftoolsControl = false;

    public static CreativeTabs tabDeepResonance = new CompatCreativeTabs("DeepResonance") {

        @Override
        protected Item getItem() {
            return Item.getItemFromBlock(ModBlocks.resonatingCrystalBlock);
        }

    };

    public DeepResonance() {
        // This has to be done VERY early
        FluidRegistry.enableUniversalBucket();
    }


    private static final int CONFIG_VERSION = 1;

    private boolean readVersionConfig() {
        int oldVersion = -1;
        try {
            Configuration cfg = versionConfig;
            cfg.load();
            oldVersion = cfg.get("version", "version", -1).getInt();
            cfg.getCategory("version").remove("version");
            cfg.get("version", "version", CONFIG_VERSION).getInt();
            if (cfg.hasChanged()) {
                cfg.save();
            }
        } catch (Exception e) {
            FMLLog.log(Level.ERROR, e, "Problem loading config file!");
        }
        return oldVersion != CONFIG_VERSION;
    }


    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();
        loadTimer = new LoadTimer(logger, "DeepResonance");
        loadTimer.startPhase(e);

        rftools = Loader.isModLoaded("rftools");
        rftoolsControl = Loader.isModLoaded("rftoolscontrol");

        mainConfigDir = e.getModConfigurationDirectory();
        modConfigDir = new File(mainConfigDir.getPath() + File.separator + "deepresonance");
        versionConfig = new Configuration(new File(modConfigDir, "version.cfg"));
        config = new Configuration(new File(modConfigDir, "main.cfg"));
        File machinesFile = new File(modConfigDir, "machines.cfg");

        if (readVersionConfig()) {
            try {
                config.getConfigFile().delete();
                machinesFile.delete();
            } catch (Exception ee) {
                FMLLog.log(Level.WARN, ee, "Could not reset config file!");
            }
        }

        compatHandler = new CompatHandler(config, logger);
        compatHandler.addHandler(new ComputerCraftCompatHandler());
        configWrapper = new ConfigWrapper(new Configuration(machinesFile));
        configWrapper.registerConfigWithInnerClasses(new ConfigMachines());
        configWrapper.refresh();
        proxy.preInit(e);
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
        loadTimer.endPhase(e);
    }


    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDRGen());
    }


    /**
     * Do your mod setup. Build whatever data structures you care about. Register recipes.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        loadTimer.startPhase(e);
        proxy.init(e);
        compatHandler.init();
        configWrapper.refresh();

        if (Loader.isModLoaded("opencomputers") || Loader.isModLoaded("OpenComputers")) {
            OpenComputersIntegration.init();
        }
        loadTimer.endPhase(e);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        Logging.log("Deep Resonance: server is stopping. Shutting down gracefully");
        DRRadiationManager.clearInstance();
        DRGeneratorNetwork.clearInstance();
    }

    /**
     * Handle interaction with other mods, complete your setup based on this.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        loadTimer.startPhase(e);
        proxy.postInit(e);
        loadTimer.endPhase(e);
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer player, int bookIndex, String page) {
        GuiDeepResonanceManual.locatePage = page;
        player.openGui(DeepResonance.instance, bookIndex, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
    }

}
