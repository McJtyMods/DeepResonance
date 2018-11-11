package mcjty.deepresonance;

import elec332.core.main.ElecCoreRegistrar;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.commands.CommandDRGen;
import mcjty.deepresonance.compat.CompatHandler;
import mcjty.deepresonance.integration.computers.OpenComputersIntegration;
import mcjty.deepresonance.items.manual.GuiDeepResonanceManual;
import mcjty.deepresonance.proxy.CommonProxy;
import mcjty.deepresonance.tanks.TankGridHandler;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.varia.Logging;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = DeepResonance.MODID, name="DeepResonance",
        dependencies =
                        "required-after:mcjtylib_ng@[" + DeepResonance.MIN_MCJTYLIB_VER + ",);" +
                        "required-after:eleccore@[" + DeepResonance.MIN_ELECCORE_VER + ",);" +
                        "after:forge@[" + DeepResonance.MIN_FORGE11_VER + ",);" +
                        "after:opencomputers@[" + DeepResonance.MIN_OPENCOMPUTERS_VER + ",);" +
                        "after:rftools@[" + DeepResonance.MIN_RFTOOLS_VER + ",)",
        acceptedMinecraftVersions = "[1.12,1.13)",
        version = DeepResonance.VERSION)
public class DeepResonance implements ModBase {
    public static final String MODID = "deepresonance";
    public static final String VERSION = "1.7.2";
    public static final String MIN_ELECCORE_VER = "1.8.434";
    public static final String MIN_OPENCOMPUTERS_VER = "1.6.0";
    public static final String MIN_FORGE11_VER = "13.19.0.2176";
    public static final String MIN_MCJTYLIB_VER = "3.0.5";
    public static final String MIN_RFTOOLS_VER = "7.58";

    @SidedProxy(clientSide="mcjty.deepresonance.proxy.ClientProxy", serverSide="mcjty.deepresonance.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance("deepresonance")
    public static DeepResonance instance;
    public static Logger logger;
    public static File mainConfigDir;
    public static File modConfigDir;
    public static Configuration config;
    public static CompatHandler compatHandler;

    public boolean rftools = false;
    public boolean rftoolsControl = false;

    public static CreativeTabs tabDeepResonance = new CreativeTabs("DeepResonance") {

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.resonatingCrystalBlock);
        }
    };

    public DeepResonance() {
        // This has to be done VERY early
        FluidRegistry.enableUniversalBucket();
    }

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();

        rftools = Loader.isModLoaded("rftools");
        rftoolsControl = Loader.isModLoaded("rftoolscontrol");

        mainConfigDir = e.getModConfigurationDirectory();
        modConfigDir = new File(mainConfigDir.getPath() + File.separator + "deepresonance");
        config = new Configuration(new File(modConfigDir, "main.cfg"));

//        compatHandler = new CompatHandler(config, logger);
//        compatHandler.addHandler(new ComputerCraftCompatHandler());
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
        proxy.init(e);
        // @todo compathandler?
//        compatHandler.init();

        if (Loader.isModLoaded("opencomputers")) {
            OpenComputersIntegration.init();
        }
    }

    /**
     * Handle interaction with other mods, complete your setup based on this.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
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
