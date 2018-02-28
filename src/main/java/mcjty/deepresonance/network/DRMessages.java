package mcjty.deepresonance.network;

import mcjty.lib.network.PacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class DRMessages {

    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
        net.registerMessage(PacketGetGeneratorInfo.Handler.class, PacketGetGeneratorInfo.class, PacketHandler.nextPacketID(), Side.SERVER);
        net.registerMessage(PacketGetCrystalInfo.Handler.class, PacketGetCrystalInfo.class, PacketHandler.nextPacketID(), Side.SERVER);
        net.registerMessage(PacketGetRadiationLevel.Handler.class, PacketGetRadiationLevel.class, PacketHandler.nextPacketID(), Side.SERVER);
        net.registerMessage(PacketGetTankInfo.Handler.class, PacketGetTankInfo.class, PacketHandler.nextPacketID(), Side.SERVER);

        // Client side
        net.registerMessage(PacketReturnGeneratorInfo.Handler.class,PacketReturnGeneratorInfo.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketReturnCrystalInfo.Handler.class, PacketReturnCrystalInfo.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketReturnRadiation.Handler.class, PacketReturnRadiation.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketReturnTankInfo.Handler.class, PacketReturnTankInfo.class, PacketHandler.nextPacketID(), Side.CLIENT);
    }

}
