package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.radiation.network.PacketGetRadiationLevel;
import mcjty.deepresonance.modules.radiation.network.PacketReturnRadiation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class DeepResonanceMessages {

    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(DeepResonance.MODID)
                .versioned("1.0")
                .optional();
        registrar.playToServer(PacketGetRadiationLevel.TYPE, PacketGetRadiationLevel.CODEC, PacketGetRadiationLevel::handle);
        registrar.playToClient(PacketReturnRadiation.TYPE, PacketReturnRadiation.CODEC, PacketReturnRadiation::handle);
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T packet, Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer)player, packet);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        PacketDistributor.sendToServer(packet);
    }
}
