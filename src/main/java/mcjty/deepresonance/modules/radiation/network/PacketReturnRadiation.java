package mcjty.deepresonance.modules.radiation.network;

import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketReturnRadiation {
    private final float strength;

    public PacketReturnRadiation(FriendlyByteBuf buf) {
        strength = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(strength);
    }

    public float getStrength() {
        return strength;
    }

    public PacketReturnRadiation(float strength) {
        this.strength = strength;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            RadiationMonitorItem.radiationStrength = strength;
        });
        ctx.setPacketHandled(true);
    }
}
