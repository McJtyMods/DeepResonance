package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ClientSetup {

    public static final ResourceLocation REDHALO = new ResourceLocation(DeepResonance.MODID, "effects/redhalo");
    public static final ResourceLocation HALO = new ResourceLocation(DeepResonance.MODID, "effects/halo");
    public static final ResourceLocation LASERBEAM = new ResourceLocation(DeepResonance.MODID, "effects/laserbeam");

    public static void initClient() {
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
            return;
        }
        event.addSprite(REDHALO);
        event.addSprite(HALO);
        event.addSprite(LASERBEAM);
    }

}
