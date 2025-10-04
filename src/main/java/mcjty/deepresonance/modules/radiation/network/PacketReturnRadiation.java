package mcjty.deepresonance.modules.radiation.network;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketReturnRadiation(float strength) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "returnradiation");
    public static final CustomPacketPayload.Type<PacketReturnRadiation> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketReturnRadiation> CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeFloat(packet.strength()),
            buf -> new PacketReturnRadiation(buf.readFloat())
    );

    public static PacketReturnRadiation create(float strength) {
        return new PacketReturnRadiation(strength);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> RadiationMonitorItem.radiationStrength = strength);
    }
}
