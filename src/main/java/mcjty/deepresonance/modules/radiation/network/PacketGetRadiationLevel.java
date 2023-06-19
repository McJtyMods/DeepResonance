package mcjty.deepresonance.modules.radiation.network;

import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import mcjty.deepresonance.setup.DeepResonanceMessages;
import mcjty.lib.varia.LevelTools;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGetRadiationLevel {

    private final GlobalPos coordinate;

    public PacketGetRadiationLevel(FriendlyByteBuf buf) {
        ResourceKey<Level> id = LevelTools.getId(buf.readResourceLocation());
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        coordinate = GlobalPos.of(id, new BlockPos(x, y, z));
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(coordinate.dimension().location());
        buf.writeInt(coordinate.pos().getX());
        buf.writeInt(coordinate.pos().getY());
        buf.writeInt(coordinate.pos().getZ());
    }

    public PacketGetRadiationLevel(GlobalPos coordinate) {
        this.coordinate = coordinate;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            Level world = player.level();
            float strength = RadiationMonitorItem.calculateRadiationStrength(world, coordinate);
            PacketReturnRadiation packet = new PacketReturnRadiation(strength);
            DeepResonanceMessages.INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.setPacketHandled(true);
    }
}
