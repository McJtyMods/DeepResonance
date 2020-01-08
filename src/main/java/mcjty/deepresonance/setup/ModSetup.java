package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 6-1-2020
 */
public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("deepresonance", () -> new ItemStack(Items.CARVED_PUMPKIN));
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
    }

    @Override
    public Logger getLogger() {
        return DeepResonance.logger; //A nicer logger
    }

}
