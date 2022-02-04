package mcjty.deepresonance.modules.radiation.network;

import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import mcjty.deepresonance.setup.DeepResonanceMessages;
import mcjty.lib.varia.LevelTools;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGetRadiationLevel {

    private final GlobalPos coordinate;

    public PacketGetRadiationLevel(PacketBuffer buf) {
        RegistryKey<World> id = LevelTools.getId(buf.readResourceLocation());
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        coordinate = GlobalPos.of(id, new BlockPos(x, y, z));
    }

    public void toBytes(PacketBuffer buf) {
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
            ServerPlayerEntity player = ctx.getSender();
            World world = player.getLevel();
            float strength = RadiationMonitorItem.calculateRadiationStrength(world, coordinate);
            PacketReturnRadiation packet = new PacketReturnRadiation(strength);
            DeepResonanceMessages.INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.setPacketHandled(true);
    }
}
