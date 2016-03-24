package mcjty.deepresonance.proxy;

import elec332.core.client.IIconRegistrar;
import elec332.core.client.ITextureLoader;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.RadiationOverlayRenderer;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.gencontroller.ControllerSounds;
import mcjty.deepresonance.client.gui.NoRFFoundException;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.items.ModItems;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy implements ITextureLoader {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        OBJLoader.INSTANCE.addDomain(DeepResonance.MODID);
        ModBlocks.initModels();
        ModItems.initModels();
        ControllerSounds.init();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
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
    public void renderGameOverlayEvent(RenderGameOverlayEvent evt) {
        RadiationOverlayRenderer.onRender(evt);
    }

    @Override
    public void registerTextures(IIconRegistrar iIconRegistrar) {
        DRFluidRegistry.registerIcons(iIconRegistrar);
    }

}
