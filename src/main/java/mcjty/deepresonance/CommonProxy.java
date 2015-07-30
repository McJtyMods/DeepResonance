package mcjty.deepresonance;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.deepresonance.worldgen.WorldGenConfiguration;
import mcjty.network.PacketHandler;
import mcjty.varia.WrenchChecker;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.io.File;

public abstract class CommonProxy {

    public static File modConfigDir;
    private Configuration mainConfig;

    public void preInit(FMLPreInitializationEvent e) {
        modConfigDir = e.getModConfigurationDirectory();
        mainConfig = new Configuration(new File(modConfigDir.getPath() + File.separator + "deepresonance", "main.cfg"));

        readMainConfig();

        PacketHandler.registerMessages("deepresonance");
//        RFToolsMessages.registerNetworkMessages();

//        ModItems.init();
//        ModBlocks.init();
//        ModCrafting.init();
//        ModDimensions.init();
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(WorldGenConfiguration.CATEGORY_WORLDGEN, "Worldgen");
            WorldGenConfiguration.init(cfg);
        } catch (Exception e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(DeepResonance.instance, new GuiProxy());
    }

    public void postInit(FMLPostInitializationEvent e) {
        mainConfig = null;
        WrenchChecker.init();
    }

}
