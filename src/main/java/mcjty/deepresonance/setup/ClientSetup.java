package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ClientSetup {

    public static final ResourceLocation REDHALO = rl("block/effects/redhalo");
    public static final ResourceLocation HALO = rl("block/effects/halo");
    public static final ResourceLocation LASERBEAM = rl("block/effects/laserbeam");

    public static final ResourceLocation[] LASERBEAMS = new ResourceLocation[]{
            rl("block/effects/laserbeam1"),
            rl("block/effects/laserbeam2"),
            rl("block/effects/laserbeam3"),
            rl("block/effects/laserbeam4")
    };

    public static final ResourceLocation BLUELASER = rl("block/effects/bluelaserbeam");
    public static final ResourceLocation REDLASER = rl("block/effects/redlaserbeam");
    public static final ResourceLocation GREENLASER = rl("block/effects/greenlaserbeam");
    public static final ResourceLocation YELLOWLASER = rl("block/effects/yellowlaserbeam");

    public static void initClient() {
    }

    public static List<ResourceLocation> onTextureStitch() {
        return List.of(REDHALO, HALO, LASERBEAM, LASERBEAMS[0], LASERBEAMS[1], LASERBEAMS[2], LASERBEAMS[3],
                BLUELASER, REDLASER, GREENLASER, YELLOWLASER);
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, path);
    }

}
