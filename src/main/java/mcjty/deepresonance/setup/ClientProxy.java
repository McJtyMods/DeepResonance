package mcjty.deepresonance.setup;

import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.ITextureLoader;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.RadiationOverlayRenderer;
import mcjty.deepresonance.client.sound.GeneratorSoundController;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.lib.setup.DefaultClientProxy;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends DefaultClientProxy implements ITextureLoader {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(this);
        OBJLoader.INSTANCE.addDomain(DeepResonance.MODID);
        GeneratorSoundController.init();
    }

    @SubscribeEvent
    public void renderGameOverlayEvent(RenderGameOverlayEvent evt) {
        RadiationOverlayRenderer.onRender(evt);
    }

    @Override
    public void registerTextures(IIconRegistrar iIconRegistrar) {
        DRFluidRegistry.registerIcons(iIconRegistrar);
    }
}