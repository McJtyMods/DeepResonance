package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ClientSetup {

    public static final ResourceLocation REDHALO = new ResourceLocation(DeepResonance.MODID, "effects/redhalo");
    public static final ResourceLocation HALO = new ResourceLocation(DeepResonance.MODID, "effects/halo");
    public static final ResourceLocation LASERBEAM = new ResourceLocation(DeepResonance.MODID, "effects/laserbeam");

    public static final ResourceLocation LASERBEAMS[] = new ResourceLocation[]{
            new ResourceLocation(DeepResonance.MODID, "effects/laserbeam1"),
            new ResourceLocation(DeepResonance.MODID, "effects/laserbeam2"),
            new ResourceLocation(DeepResonance.MODID, "effects/laserbeam3"),
            new ResourceLocation(DeepResonance.MODID, "effects/laserbeam4")
    };

    public static final ResourceLocation BLUELASER = new ResourceLocation(DeepResonance.MODID, "effects/bluelaserbeam");
    public static final ResourceLocation REDLASER = new ResourceLocation(DeepResonance.MODID, "effects/redlaserbeam");
    public static final ResourceLocation GREENLASER = new ResourceLocation(DeepResonance.MODID, "effects/greenlaserbeam");
    public static final ResourceLocation YELLOWLASER = new ResourceLocation(DeepResonance.MODID, "effects/yellowlaserbeam");

    public static void initClient() {
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
            return;
        }
        event.addSprite(REDHALO);
        event.addSprite(HALO);
        event.addSprite(LASERBEAM);
        event.addSprite(LASERBEAMS[0]);
        event.addSprite(LASERBEAMS[1]);
        event.addSprite(LASERBEAMS[2]);
        event.addSprite(LASERBEAMS[3]);
        event.addSprite(BLUELASER);
        event.addSprite(REDLASER);
        event.addSprite(GREENLASER);
        event.addSprite(YELLOWLASER);

    }

}
