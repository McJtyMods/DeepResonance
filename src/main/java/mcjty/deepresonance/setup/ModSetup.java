package mcjty.deepresonance.setup;

import mcjty.deepresonance.commands.ModCommands;
import mcjty.deepresonance.modules.radiation.manager.RadiationTickEvent;
import mcjty.lib.setup.DefaultModSetup;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ModSetup extends DefaultModSetup {

    @Override
    public void init() {
        super.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> ModCommands.register(dispatcher));
        ServerTickEvents.END_LEVEL_TICK.register(new RadiationTickEvent()::onTick);
    }

    @Override
    protected void setupModCompat() {
    }
}
