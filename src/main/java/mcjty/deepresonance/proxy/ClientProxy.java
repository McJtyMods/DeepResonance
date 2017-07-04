package mcjty.deepresonance.proxy;

import com.google.common.util.concurrent.ListenableFuture;
import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.ITextureLoader;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.RadiationOverlayRenderer;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.client.gui.NoRFFoundException;
import mcjty.deepresonance.client.sound.GeneratorSoundController;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.Callable;

public class ClientProxy extends CommonProxy implements ITextureLoader {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(this);
        OBJLoader.INSTANCE.addDomain(DeepResonance.MODID);
        GeneratorSoundController.init();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModBlocks.initModels();
        ModItems.initModels();
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

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
        return Minecraft.getMinecraft().addScheduledTask(callableToSchedule);
    }

    @Override
    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
        return Minecraft.getMinecraft().addScheduledTask(runnableToSchedule);
    }
}
