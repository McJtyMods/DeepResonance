package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.radiation.network.PacketGetRadiationLevel;
import mcjty.deepresonance.modules.radiation.network.PacketReturnRadiation;
import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.IPayloadRegistrar;
import mcjty.lib.network.Networking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;

public class DeepResonanceMessages {

    private static IPayloadRegistrar registrar;

    public static void registerMessages() {
        registrar = Networking.registrar(DeepResonance.MODID)
                .versioned("1.0")
                .optional();
        registrar.play(PacketGetRadiationLevel.class, PacketGetRadiationLevel::create, handler -> handler.server(PacketGetRadiationLevel::handle));
        registrar.play(PacketReturnRadiation.class, PacketReturnRadiation::create, handler -> handler.client(PacketReturnRadiation::handle));
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T packet, Player player) {
        registrar.getChannel().sendTo(packet, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        registrar.getChannel().sendToServer(packet);
    }
}
