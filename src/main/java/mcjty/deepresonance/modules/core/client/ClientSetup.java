package mcjty.deepresonance.modules.core.client;

import mcjty.deepresonance.modules.core.CoreModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ClientSetup {
    public static void initClient() {
        RenderTypeLookup.setRenderLayer(CoreModule.RESONATING_CRYSTAL_BLOCK.get(), RenderType.translucent());
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
            return;
        }
        event.addSprite(ResonatingCrystalTER.REDHALO);
    }

}
