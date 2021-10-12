package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.commands.ModCommands;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Logger;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("deepresonance", () -> new ItemStack(CoreModule.RESONATING_CRYSTAL_ITEM.get())); //resonating crystal
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        DeepResonanceMessages.registerMessages("deepresonance");
    }

    @Override
    protected void setupModCompat() {
    }

    @Override
    public Logger getLogger() {
        return DeepResonance.logger; //A nicer logger
    }

    public void registerCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

}
