package mcjty.deepresonance.network;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.network.PacketHandler;
import net.minecraftforge.fml.relauncher.Side;

public class DRMessages {

    public static void registerNetworkMessages() {

        int i = PacketHandler.registerMessages(DeepResonance.networkHandler.getNetworkWrapper());
        PacketHandler.modNetworking.put(DeepResonance.MODID, DeepResonance.networkHandler.getNetworkWrapper());
        DeepResonance.networkHandler.setMessageIndex(i);

        // Server side
        DeepResonance.networkHandler.registerPacket(PacketGetGeneratorInfo.Handler.class, PacketGetGeneratorInfo.class, Side.SERVER);
        DeepResonance.networkHandler.registerPacket(PacketGetCrystalInfo.Handler.class, PacketGetCrystalInfo.class, Side.SERVER);
        DeepResonance.networkHandler.registerPacket(PacketGetRadiationLevel.Handler.class, PacketGetRadiationLevel.class, Side.SERVER);
        DeepResonance.networkHandler.registerPacket(PacketGetTankInfo.Handler.class, PacketGetTankInfo.class, Side.SERVER);

        // Client side
        DeepResonance.networkHandler.registerPacket(PacketReturnGeneratorInfo.Handler.class,PacketReturnGeneratorInfo.class, Side.CLIENT);
        DeepResonance.networkHandler.registerPacket(PacketReturnCrystalInfo.Handler.class, PacketReturnCrystalInfo.class, Side.CLIENT);
        DeepResonance.networkHandler.registerPacket(PacketReturnRadiation.Handler.class, PacketReturnRadiation.class, Side.CLIENT);
        DeepResonance.networkHandler.registerPacket(PacketReturnTankInfo.Handler.class, PacketReturnTankInfo.class, Side.CLIENT);
    }

}
