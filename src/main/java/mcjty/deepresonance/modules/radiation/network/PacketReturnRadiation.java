package mcjty.deepresonance.modules.radiation.network;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PacketReturnRadiation(float strength) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(DeepResonance.MODID, "returnradiation");

    public static PacketReturnRadiation create(FriendlyByteBuf buf) {
        return new PacketReturnRadiation(buf.readFloat());
    }

    public static PacketReturnRadiation create(float strength) {
        return new PacketReturnRadiation(strength);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(strength);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            RadiationMonitorItem.radiationStrength = strength;
        });
    }
}
