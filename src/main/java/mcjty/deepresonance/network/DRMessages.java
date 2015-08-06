package mcjty.deepresonance.network;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import mcjty.network.PacketHandler;

public class DRMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
        net.registerMessage(PacketGetGeneratorInfo.class, PacketGetGeneratorInfo.class, PacketHandler.nextID(), Side.SERVER);

        // Client side
        net.registerMessage(PacketReturnGeneratorInfoHandler.class, PacketReturnGeneratorInfo.class, PacketHandler.nextID(), Side.CLIENT);
    }
}
