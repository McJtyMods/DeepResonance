package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.resources.Identifier;

public class ClientSetup {

    public static final Identifier REDHALO = rl("block/effects/redhalo");
    public static final Identifier HALO = rl("block/effects/halo");
    public static final Identifier LASERBEAM = rl("block/effects/laserbeam");

    public static final Identifier[] LASERBEAMS = new Identifier[]{
            rl("block/effects/laserbeam1"),
            rl("block/effects/laserbeam2"),
            rl("block/effects/laserbeam3"),
            rl("block/effects/laserbeam4")
    };

    public static final Identifier BLUELASER = rl("block/effects/bluelaserbeam");
    public static final Identifier REDLASER = rl("block/effects/redlaserbeam");
    public static final Identifier GREENLASER = rl("block/effects/greenlaserbeam");
    public static final Identifier YELLOWLASER = rl("block/effects/yellowlaserbeam");

    private static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(DeepResonance.MODID, path);
    }

}
