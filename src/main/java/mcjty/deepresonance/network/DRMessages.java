package mcjty.deepresonance.network;

import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import cpw.mods.fml.relauncher.Side;
import elec332.core.network.NetworkHandler;
import mcjty.deepresonance.DeepResonance;

import java.util.List;

public class DRMessages {

    private static List<Class> mcJtyLibPackets = Lists.newArrayList();

    public static void registerNetworkMessages() {
        discoverPackets();
        for (Class clazz : mcJtyLibPackets)
            DeepResonance.networkHandler.registerPacket(clazz, getSideForPacket(clazz));

        // Server side
        DeepResonance.networkHandler.registerPacket(PacketGetGeneratorInfo.class, Side.SERVER);
        DeepResonance.networkHandler.registerPacket(PacketGetCrystalInfo.class, Side.SERVER);
        DeepResonance.networkHandler.registerPacket(PacketGetRadiationLevel.class, Side.SERVER);

        // Client side
        DeepResonance.networkHandler.registerPacket(PacketReturnGeneratorInfoHandler.class,PacketReturnGeneratorInfo.class, Side.CLIENT);
        DeepResonance.networkHandler.registerPacket(PacketReturnCrystalInfoHandler.class, PacketReturnCrystalInfo.class, Side.CLIENT);
        DeepResonance.networkHandler.registerPacket(PacketReturnRadiationHandler.class, PacketReturnRadiation.class, Side.CLIENT);
    }

    private static void discoverPackets(){
        try {
            for (ClassPath.ClassInfo classInfo : ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClasses("mcjty.network")) {
                Class clazz = Class.forName(classInfo.getName());
                if (NetworkHandler.isValidPacket(clazz))
                    mcJtyLibPackets.add(clazz);
            }
        } catch (Exception e){
            throw new RuntimeException("[DeepResonance] Error fetching packets!", e);
        }
    }

    private static Side getSideForPacket(Class packetClass){
        String name = packetClass.getName();
        if (name.contains("From") && name.contains("Server") && !name.contains("Request"))
            return Side.CLIENT;
        return Side.SERVER;
    }

}
