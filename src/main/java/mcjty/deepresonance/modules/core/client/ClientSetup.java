package mcjty.deepresonance.modules.core.client;

import mcjty.deepresonance.modules.core.CoreModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

public class ClientSetup {
    public static void initClient() {
        ItemBlockRenderTypes.setRenderLayer(CoreModule.RESONATING_CRYSTAL_GENERATED.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(CoreModule.RESONATING_CRYSTAL_GENERATED_EMPTY.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(CoreModule.RESONATING_CRYSTAL_NATURAL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(CoreModule.RESONATING_CRYSTAL_NATURAL_EMPTY.get(), RenderType.translucent());
    }
}
