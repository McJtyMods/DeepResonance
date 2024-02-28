package mcjty.deepresonance.setup;

import mcjty.deepresonance.ForgeEventHandlers;
import mcjty.deepresonance.commands.ModCommands;
import mcjty.deepresonance.compat.rftoolscontrol.RFToolsControlSupport;
import mcjty.deepresonance.modules.radiation.manager.RadiationTickEvent;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.lib.varia.Logging;
import net.neoforged.neoforge.common.MinecraftForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.fml.InterModComms;
import net.neoforged.neoforge.fml.ModList;
import net.neoforged.neoforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        MinecraftForge.EVENT_BUS.register(new RadiationTickEvent());
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        DeepResonanceMessages.registerMessages();
    }

    @Override
    protected void setupModCompat() {
        if (ModList.get().isLoaded("rftoolscontrol")) {
            Logging.log("Detected RFTools Control: enabling support");
            InterModComms.sendTo("rftoolscontrol", "getOpcodeRegistry", RFToolsControlSupport.GetOpcodeRegistry::new);
        }
    }

    public void registerCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

}
