package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.util.DeepResonanceWorldEventHandler;
import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 6-1-2020
 */
public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("deepresonance", () -> new ItemStack(CoreModule.RESONATING_CRYSTAL_ITEM.get())); //resonating crystal
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(new DeepResonanceWorldEventHandler());
    }

    public void clientSetup(FMLClientSetupEvent event) {
        OBJLoader.INSTANCE.addDomain(DeepResonance.MODID);
    }

    @Override
    protected void setupModCompat() {
    }

    @Override
    public Logger getLogger() {
        return DeepResonance.logger; //A nicer logger
    }

}
