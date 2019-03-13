package mcjty.deepresonance.setup;

import elec332.core.handler.ElecCoreRegistrar;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.ForgeEventHandlers;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.config.ConfigSetup;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.compat.computers.OpenComputersIntegration;
import mcjty.deepresonance.items.ModItems;
import mcjty.deepresonance.network.DRMessages;
import mcjty.deepresonance.radiation.RadiationTickEvent;
import mcjty.deepresonance.tanks.TankGridHandler;
import mcjty.deepresonance.worldgen.WorldGen;
import mcjty.deepresonance.worldgen.WorldTickHandler;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.lib.varia.Logging;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ModSetup extends DefaultModSetup {

    public boolean rftools = false;
    public boolean rftoolsControl = false;


    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        MinecraftForge.EVENT_BUS.register(WorldTickHandler.instance);
        MinecraftForge.EVENT_BUS.register(new RadiationTickEvent());
        NetworkRegistry.INSTANCE.registerGuiHandler(DeepResonance.instance, new GuiProxy());

        ConfigSetup.init();

        DRMessages.registerMessages("deepresonance");

        ElecCoreRegistrar.GRIDHANDLERS.register(new TankGridHandler());

        ModItems.init();
        ModBlocks.init();
        WorldGen.init();
        DRFluidRegistry.initFluids();

    }

    @Override
    protected void setupModCompat() {
        rftools = Loader.isModLoaded("rftools");
        rftoolsControl = Loader.isModLoaded("rftoolscontrol");

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

        if (Loader.isModLoaded("opencomputers")) {
            OpenComputersIntegration.init();
        }

        //@todo
//        FMLInterModComms.sendMessage("rftools", "dimlet_configure", "Material.tile.oreResonating=30000,6000,400,5");
    }

    @Override
    public void createTabs() {
        createTab("DeepResonance", new ItemStack(ModBlocks.resonatingCrystalBlock));
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        ConfigSetup.postInit();
    }
}
