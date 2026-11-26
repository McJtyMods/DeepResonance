package mcjty.deepresonance.setup;

import mcjty.deepresonance.modules.radiation.network.PacketGetRadiationLevel;
import mcjty.deepresonance.modules.radiation.network.PacketReturnRadiation;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PayloadContext;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class DeepResonanceMessages {

    public static void registerMessages() {
        PayloadTypeRegistry.serverboundPlay().register(PacketGetRadiationLevel.TYPE, PacketGetRadiationLevel.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PacketReturnRadiation.TYPE, PacketReturnRadiation.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketGetRadiationLevel.TYPE, (packet, context) ->
                packet.handle(new PayloadContext(context.player(), context.server())));
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T packet, Player player) {
        Networking.sendToPlayer(packet, player);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        Networking.sendToServer(packet);
    }
}
