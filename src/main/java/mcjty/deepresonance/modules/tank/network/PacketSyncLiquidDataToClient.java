package mcjty.deepresonance.modules.tank.network;


import mcjty.deepresonance.util.LiquidCrystalData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncLiquidDataToClient {

    private int tankBlobId;
    private LiquidCrystalData data;

    public PacketSyncLiquidDataToClient(PacketBuffer buf) {
    }

    public PacketSyncLiquidDataToClient(int tankBlobId, LiquidCrystalData data) {
        this.tankBlobId = tankBlobId;
        this.data = data;
    }

    public void toBytes(PacketBuffer buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
        });
        ctx.setPacketHandled(true);
    }
}
