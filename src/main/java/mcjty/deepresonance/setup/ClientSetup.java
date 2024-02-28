package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.TextureStitchEvent;

import java.util.List;

public class ClientSetup {

    public static final ResourceLocation REDHALO = new ResourceLocation(DeepResonance.MODID, "block/effects/redhalo");
    public static final ResourceLocation HALO = new ResourceLocation(DeepResonance.MODID, "block/effects/halo");
    public static final ResourceLocation LASERBEAM = new ResourceLocation(DeepResonance.MODID, "block/effects/laserbeam");

    public static final ResourceLocation[] LASERBEAMS = new ResourceLocation[]{
            new ResourceLocation(DeepResonance.MODID, "block/effects/laserbeam1"),
            new ResourceLocation(DeepResonance.MODID, "block/effects/laserbeam2"),
            new ResourceLocation(DeepResonance.MODID, "block/effects/laserbeam3"),
            new ResourceLocation(DeepResonance.MODID, "block/effects/laserbeam4")
    };

    public static final ResourceLocation BLUELASER = new ResourceLocation(DeepResonance.MODID, "block/effects/bluelaserbeam");
    public static final ResourceLocation REDLASER = new ResourceLocation(DeepResonance.MODID, "block/effects/redlaserbeam");
    public static final ResourceLocation GREENLASER = new ResourceLocation(DeepResonance.MODID, "block/effects/greenlaserbeam");
    public static final ResourceLocation YELLOWLASER = new ResourceLocation(DeepResonance.MODID, "block/effects/yellowlaserbeam");

    public static void initClient() {
    }

    public static List<ResourceLocation> onTextureStitch() {
        return List.of(REDHALO, HALO, LASERBEAM, LASERBEAMS[0], LASERBEAMS[1], LASERBEAMS[2], LASERBEAMS[3],
                BLUELASER, REDLASER, GREENLASER, YELLOWLASER);
    }

}
