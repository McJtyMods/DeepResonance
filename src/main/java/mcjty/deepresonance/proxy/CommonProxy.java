package mcjty.deepresonance.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.FMLEventHandlers;
import mcjty.deepresonance.ForgeEventHandlers;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.generator.GeneratorConfiguration;
import mcjty.deepresonance.blocks.laser.LaserBonusConfiguration;
import mcjty.deepresonance.crafting.ModCrafting;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.deepresonance.items.ModItems;
import mcjty.deepresonance.network.DRMessages;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import mcjty.deepresonance.radiation.RadiationTickEvent;
import mcjty.deepresonance.worldgen.WorldGen;
import mcjty.deepresonance.worldgen.WorldGenConfiguration;
import mcjty.deepresonance.worldgen.WorldTickHandler;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.varia.WrenchChecker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

public abstract class CommonProxy {

    private Configuration mainConfig;

    public void preInit(FMLPreInitializationEvent e) {
        GeneralConfig.preInit(e);
        mainConfig = DeepResonance.config;
        readMainConfig();
        DRMessages.registerNetworkMessages();
        DRFluidRegistry.preInitFluids();
        ModItems.init();
        ModBlocks.init();
        ModCrafting.init();
        WorldGen.init();
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(WorldGenConfiguration.CATEGORY_WORLDGEN, "Configuration for wodlgen");
            cfg.addCustomCategoryComment(GeneratorConfiguration.CATEGORY_GENERATOR, "Configuration for the generator multiblock");
            cfg.addCustomCategoryComment(RadiationConfiguration.CATEGORY_RADIATION, "Configuration for the radiation");
            cfg.addCustomCategoryComment(LaserBonusConfiguration.CATEGORY_LASERBONUS, "Configuration for the laser bonuses");
            WorldGenConfiguration.init(cfg);
            GeneratorConfiguration.init(cfg);
            RadiationConfiguration.init(cfg);
            LaserBonusConfiguration.init(cfg);
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
        FMLCommonHandler.instance().bus().register(WorldTickHandler.instance);
        FMLCommonHandler.instance().bus().register(new RadiationTickEvent());
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        FMLCommonHandler.instance().bus().register(new FMLEventHandlers());
    }

    public void postInit(FMLPostInitializationEvent e) {
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
        mainConfig = null;
        WrenchChecker.init();
    }

    public abstract void throwException(Exception e, int i);

}
