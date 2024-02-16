package mcjty.deepresonance.modules.radiation.network;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import mcjty.deepresonance.setup.DeepResonanceMessages;
import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import mcjty.lib.varia.LevelTools;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record PacketGetRadiationLevel(GlobalPos coordinate) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(DeepResonance.MODID, "getradiationlevel");

    public static PacketGetRadiationLevel create(FriendlyByteBuf buf) {
        ResourceKey<Level> id = LevelTools.getId(buf.readResourceLocation());
        return new PacketGetRadiationLevel(GlobalPos.of(id, buf.readBlockPos()));
    }

    public static PacketGetRadiationLevel create(GlobalPos coordinate) {
        return new PacketGetRadiationLevel(coordinate);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(coordinate.dimension().location());
        buf.writeBlockPos(coordinate.pos());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent( player -> {
                Level world = player.level();
                float strength = RadiationMonitorItem.calculateRadiationStrength(world, coordinate);
                DeepResonanceMessages.sendToPlayer(new PacketReturnRadiation(strength), player);
            });
        });
    }
}
