package mcjty.deepresonance.modules.radiation.network;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import mcjty.deepresonance.setup.DeepResonanceMessages;
import mcjty.lib.varia.LevelTools;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketGetRadiationLevel(GlobalPos coordinate) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(DeepResonance.MODID, "getradiationlevel");
    public static final CustomPacketPayload.Type<PacketGetRadiationLevel> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketGetRadiationLevel> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeIdentifier(packet.coordinate().dimension().identifier());
                buf.writeBlockPos(packet.coordinate().pos());
            },
            buf -> {
                ResourceKey<Level> id = LevelTools.getId(buf.readIdentifier());
                return new PacketGetRadiationLevel(GlobalPos.of(id, buf.readBlockPos()));
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketGetRadiationLevel create(GlobalPos coordinate) {
        return new PacketGetRadiationLevel(coordinate);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level world = ctx.player().level();
            float strength = RadiationMonitorItem.calculateRadiationStrength(world, coordinate);
            DeepResonanceMessages.sendToPlayer(new PacketReturnRadiation(strength), ctx.player());
        });
    }
}
