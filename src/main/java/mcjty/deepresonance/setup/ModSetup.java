package mcjty.deepresonance.setup;

import mcjty.deepresonance.ForgeEventHandlers;
import mcjty.deepresonance.commands.ModCommands;
import mcjty.deepresonance.compat.rftoolscontrol.RFToolsControlSupport;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.radiation.manager.RadiationTickEvent;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.lib.varia.Logging;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("deepresonance", () -> new ItemStack(CoreModule.RESONATING_CRYSTAL_GENERATED.get())); //resonating crystal
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        MinecraftForge.EVENT_BUS.register(new RadiationTickEvent());
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        DeepResonanceMessages.registerMessages("deepresonance");
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
