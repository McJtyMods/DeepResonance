package mcjty.deepresonance.proxy;

import mcjty.deepresonance.RadiationOverlayRenderer;
import mcjty.deepresonance.client.gui.NoRFFoundException;
import mcjty.deepresonance.client.render.ModRenderers;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        ModRenderers.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @Override
    public void throwException(Exception e, int i) {
        switch (i){
            case 0:
                throw new NoRFFoundException(e);
            default:
                throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public void registerIcons(TextureStitchEvent.Pre event){
        if (event.map.getTextureType() == 0)
            DRFluidRegistry.registerIcons(event.map);
    }

    @SubscribeEvent
    public void renderGameOverlayEvent(RenderGameOverlayEvent evt) {
        RadiationOverlayRenderer.onRender(evt);
    }

}
