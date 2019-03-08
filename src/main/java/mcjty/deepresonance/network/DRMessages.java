package mcjty.deepresonance.network;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.network.PacketHandler;
import mcjty.lib.thirteen.ChannelBuilder;
import mcjty.lib.thirteen.SimpleChannel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class DRMessages {

    public static SimpleNetworkWrapper INSTANCE;

    public static void registerMessages(String name) {
        SimpleChannel net = ChannelBuilder
                .named(new ResourceLocation(DeepResonance.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net.getNetwork();

        // Server side
        net.registerMessageServer(id(), PacketGetGeneratorInfo.class, PacketGetGeneratorInfo::toBytes, PacketGetGeneratorInfo::new, PacketGetGeneratorInfo::handle);
        net.registerMessageServer(id(), PacketGetCrystalInfo.class, PacketGetCrystalInfo::toBytes, PacketGetCrystalInfo::new, PacketGetCrystalInfo::handle);
        net.registerMessageServer(id(), PacketGetRadiationLevel.class, PacketGetRadiationLevel::toBytes, PacketGetRadiationLevel::new, PacketGetRadiationLevel::handle);
        net.registerMessageServer(id(), PacketGetTankInfo.class, PacketGetTankInfo::toBytes, PacketGetTankInfo::new, PacketGetTankInfo::handle);

        // Client side
        net.registerMessageClient(id(), PacketReturnGeneratorInfo.class, PacketReturnGeneratorInfo::toBytes, PacketReturnGeneratorInfo::new, PacketReturnGeneratorInfo::handle);
        net.registerMessageClient(id(), PacketReturnCrystalInfo.class, PacketReturnCrystalInfo::toBytes, PacketReturnCrystalInfo::new, PacketReturnCrystalInfo::handle);
        net.registerMessageClient(id(), PacketReturnRadiation.class, PacketReturnRadiation::toBytes, PacketReturnRadiation::new, PacketReturnRadiation::handle);
        net.registerMessageClient(id(), PacketReturnTankInfo.class, PacketReturnTankInfo::toBytes, PacketReturnTankInfo::new, PacketReturnTankInfo::handle);
    }

    private static int id() {
        return PacketHandler.nextPacketID();
    }

}
