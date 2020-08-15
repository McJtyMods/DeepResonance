package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 6-1-2020
 */
public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("deepresonance", () -> new ItemStack(CoreModule.RESONATING_CRYSTAL_ITEM.get())); //resonating crystal
    }

    @Override
    protected void setupModCompat() {
    }

    @Override
    public Logger getLogger() {
        return DeepResonance.logger; //A nicer logger
    }

}
