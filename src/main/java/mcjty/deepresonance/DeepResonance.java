package mcjty.deepresonance;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.config.ConfigWrapper;
import elec332.core.network.NetworkHandler;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.commands.CommandDRGen;
import mcjty.deepresonance.compat.CompatHandler;
import mcjty.deepresonance.compat.handlers.ComputerCraftCompatHandler;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.deepresonance.grid.WorldGridRegistry;
import mcjty.deepresonance.items.manual.GuiDeepResonanceManual;
import mcjty.deepresonance.proxy.CommonProxy;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.varia.Logging;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = DeepResonance.MODID, name="DeepResonance", dependencies =
        "required-after:Forge@["+DeepResonance.MIN_FORGE_VER+
        ",);required-after:McJtyLib@["+DeepResonance.MIN_MCJTYLIB_VER+
        ",);required-after:ElecCore@["+DeepResonance.MIN_ELECCORE_VER+
        ",)",
        version = DeepResonance.VERSION)
public class DeepResonance implements ModBase {
    public static final String MODID = "deepresonance";
    public static final String VERSION = "1.1.0beta2";
    public static final String MIN_FORGE_VER = "10.13.2.1291";
    public static final String MIN_MCJTYLIB_VER = "1.7.0";
    public static final String MIN_ELECCORE_VER = "1.4.170";

    @SidedProxy(clientSide="mcjty.deepresonance.proxy.ClientProxy", serverSide="mcjty.deepresonance.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance("deepresonance")
    public static DeepResonance instance;
    public static Logger logger;
    public static File mainConfigDir;
    public static File modConfigDir;
    public static WorldGridRegistry worldGridRegistry;
    public static Configuration config;
    public static CompatHandler compatHandler;
    public static ConfigWrapper configWrapper;
    public static NetworkHandler networkHandler;

    public static CreativeTabs tabDeepResonance = new CreativeTabs("DeepResonance") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Item.getItemFromBlock(ModBlocks.resonatingCrystalBlock);
        }
    };

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();
        mainConfigDir = e.getModConfigurationDirectory();
        modConfigDir = new File(mainConfigDir.getPath() + File.separator + "deepresonance");
        config = new Configuration(new File(modConfigDir, "main.cfg"));
        worldGridRegistry = new WorldGridRegistry();
        networkHandler = new NetworkHandler(MODID);
        compatHandler = new CompatHandler(config, logger);
        compatHandler.addHandler(new ComputerCraftCompatHandler());
        configWrapper = new ConfigWrapper(new Configuration(new File(modConfigDir, "machines.cfg")));
        configWrapper.registerConfigWithInnerClasses(new ConfigMachines());
        configWrapper.refresh();
        proxy.preInit(e);
        MainCompatHandler.registerWaila();
        FMLInterModComms.sendMessage("rftools", "dimlet_configure", "Material.tile.oreResonating=30000,6000,400,5");
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
        compatHandler.init();
        configWrapper.refresh();
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
        proxy.postInit(e);
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer player, int bookIndex, String page) {
        GuiDeepResonanceManual.locatePage = page;
        player.openGui(DeepResonance.instance, bookIndex, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
    }
}
