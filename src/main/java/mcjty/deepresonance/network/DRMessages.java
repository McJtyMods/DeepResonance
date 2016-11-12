package mcjty.deepresonance.network;

import elec332.core.java.ReflectionHelper;
import elec332.core.util.FMLUtil;
import mcjty.deepresonance.DeepResonance;
import mcjty.lib.network.PacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class DRMessages {

    public static SimpleNetworkWrapper networkWrapper;

    public static void registerNetworkMessages() {

        try {
            Class clazz = FMLUtil.loadClass("elec332.core.network.impl.DefaultNetworkHandler");
            networkWrapper = (SimpleNetworkWrapper) ReflectionHelper.makeFinalFieldModifiable(clazz.getDeclaredField("networkWrapper")).get(DeepResonance.networkHandler);

            int i = PacketHandler.registerMessages(networkWrapper);
            PacketHandler.modNetworking.put(DeepResonance.MODID, networkWrapper);
            ReflectionHelper.makeFinalFieldModifiable(clazz.getDeclaredField("i")).set(DeepResonance.networkHandler, i);
        } catch (Exception e){
            throw new RuntimeException("Error registering packets, this version of DeepResonance is probably incompatible with the installed version of ElecCore.", e);
        }

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
