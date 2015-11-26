package mcjty.deepresonance.network;

import cpw.mods.fml.relauncher.Side;
import mcjty.deepresonance.DeepResonance;
import mcjty.lib.network.PacketHandler;

public class DRMessages {

    public static void registerNetworkMessages() {

        int i = PacketHandler.registerMessages(DeepResonance.networkHandler.getNetworkWrapper());
        DeepResonance.networkHandler.setMessageIndex(i);

        // Server side
        DeepResonance.networkHandler.registerPacket(PacketGetGeneratorInfo.class, Side.SERVER);
        DeepResonance.networkHandler.registerPacket(PacketGetCrystalInfo.class, Side.SERVER);
        DeepResonance.networkHandler.registerPacket(PacketGetRadiationLevel.class, Side.SERVER);
        DeepResonance.networkHandler.registerPacket(PacketGetTankInfo.class, Side.SERVER);

        // Client side
        DeepResonance.networkHandler.registerPacket(PacketReturnGeneratorInfoHandler.class,PacketReturnGeneratorInfo.class, Side.CLIENT);
        DeepResonance.networkHandler.registerPacket(PacketReturnCrystalInfoHandler.class, PacketReturnCrystalInfo.class, Side.CLIENT);
        DeepResonance.networkHandler.registerPacket(PacketReturnRadiationHandler.class, PacketReturnRadiation.class, Side.CLIENT);
        DeepResonance.networkHandler.registerPacket(PacketReturnTankInfoHandler.class, PacketReturnTankInfo.class, Side.CLIENT);
    }

}
