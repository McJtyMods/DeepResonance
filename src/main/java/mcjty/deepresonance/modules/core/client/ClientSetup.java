package mcjty.deepresonance.modules.core.client;

import mcjty.deepresonance.modules.core.CoreModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class ClientSetup {
    public static void initClient() {
        RenderTypeLookup.setRenderLayer(CoreModule.RESONATING_CRYSTAL_BLOCK.get(), RenderType.getTranslucent());
    }
}
