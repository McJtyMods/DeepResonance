package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.radiation.network.PacketGetRadiationLevel;
import mcjty.deepresonance.modules.radiation.network.PacketReturnRadiation;
import mcjty.lib.network.PacketHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class DeepResonanceMessages {

    public static SimpleChannel INSTANCE;

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

        net.registerMessage(id(), PacketGetRadiationLevel.class, PacketGetRadiationLevel::toBytes, PacketGetRadiationLevel::new, PacketGetRadiationLevel::handle);
        net.registerMessage(id(), PacketReturnRadiation.class, PacketReturnRadiation::toBytes, PacketReturnRadiation::new, PacketReturnRadiation::handle);

        PacketHandler.registerStandardMessages(id(), net);
    }
}
