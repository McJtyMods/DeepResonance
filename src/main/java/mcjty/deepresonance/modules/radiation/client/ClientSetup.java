package mcjty.deepresonance.modules.radiation.client;

import mcjty.deepresonance.modules.radiation.RadiationModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class ClientSetup {

    public static void initClient() {
        RenderTypeLookup.setRenderLayer(RadiationModule.DENSE_GLASS_BLOCK.get(), RenderType.cutout());
    }

}
