package mcjty.deepresonance.network;

import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.items.RadiationMonitorItem;
import mcjty.lib.network.IClientServerDelayed;
import mcjty.lib.thirteen.Context;
import mcjty.lib.varia.GlobalCoordinate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

public class PacketGetRadiationLevel implements IMessage, IClientServerDelayed {

    private GlobalCoordinate coordinate;

    @Override
    public void fromBytes(ByteBuf buf) {
        int dim = buf.readInt();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        coordinate = new GlobalCoordinate(new BlockPos(x, y, z), dim);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(coordinate.getDimension());
        buf.writeInt(coordinate.getCoordinate().getX());
        buf.writeInt(coordinate.getCoordinate().getY());
        buf.writeInt(coordinate.getCoordinate().getZ());
    }

    public PacketGetRadiationLevel() {
    }

    public PacketGetRadiationLevel(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketGetRadiationLevel(GlobalCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            EntityPlayerMP player = ctx.getSender();
            World world = player.getEntityWorld();
            float strength = RadiationMonitorItem.calculateRadiationStrength(world, coordinate);
            PacketReturnRadiation packet = new PacketReturnRadiation(strength);
            DRMessages.INSTANCE.sendTo(packet, ctx.getSender());
        });
        ctx.setPacketHandled(true);
    }
}