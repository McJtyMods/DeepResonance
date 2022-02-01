package mcjty.deepresonance.modules.core.client;

import mcjty.deepresonance.modules.core.CoreModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class ClientSetup {
    public static void initClient() {
        RenderTypeLookup.setRenderLayer(CoreModule.RESONATING_CRYSTAL_GENERATED.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(CoreModule.RESONATING_CRYSTAL_GENERATED_EMPTY.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(CoreModule.RESONATING_CRYSTAL_NATURAL.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(CoreModule.RESONATING_CRYSTAL_NATURAL_EMPTY.get(), RenderType.translucent());
    }
}
