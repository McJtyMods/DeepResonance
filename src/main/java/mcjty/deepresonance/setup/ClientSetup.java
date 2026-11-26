package mcjty.deepresonance.setup;

import mcjty.deepresonance.modules.radiation.client.RadiationMonitorProperty;
import mcjty.deepresonance.modules.radiation.client.RadiationOverlayRenderer;
import mcjty.deepresonance.modules.radiation.network.PacketReturnRadiation;
import mcjty.deepresonance.modules.tank.client.TankTESR;
import mcjty.lib.network.PayloadContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.resources.Identifier;

public class ClientSetup implements ClientModInitializer {

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

    @Override
    public void onInitializeClient() {
        ObjModelLoader.register();
        RadiationMonitorProperty.register();
        TankTESR.registerFluidModel();
        HudElementRegistry.addLast(rl("radiation"), RadiationOverlayRenderer::onRender);
        ClientPlayNetworking.registerGlobalReceiver(PacketReturnRadiation.TYPE, (packet, context) ->
                packet.handle(new PayloadContext(context.player(), context.client())));
        DeepResonance.modules.initClient();
    }

    private static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(DeepResonance.MODID, path);
    }

}
