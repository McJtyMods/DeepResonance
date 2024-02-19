package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.radiation.network.PacketGetRadiationLevel;
import mcjty.deepresonance.modules.radiation.network.PacketReturnRadiation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import static mcjty.lib.network.PlayPayloadContext.wrap;

public class DeepResonanceMessages {

    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void registerMessages(String name) {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(DeepResonance.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.registerMessage(id(), PacketGetRadiationLevel.class, PacketGetRadiationLevel::write, PacketGetRadiationLevel::create, wrap(PacketGetRadiationLevel::handle));
        net.registerMessage(id(), PacketReturnRadiation.class, PacketReturnRadiation::write, PacketReturnRadiation::create, wrap(PacketReturnRadiation::handle));
    }

    public static <T> void sendToPlayer(T packet, Player player) {
        INSTANCE.sendTo(packet, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T> void sendToServer(T packet) {
        INSTANCE.sendToServer(packet);
    }
}
