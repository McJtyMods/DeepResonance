package mcjty.deepresonance.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.items.RadiationMonitorItem;
import mcjty.lib.varia.Coordinate;
import mcjty.lib.varia.GlobalCoordinate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class PacketGetRadiationLevel implements IMessage,IMessageHandler<PacketGetRadiationLevel, PacketReturnRadiation> {
    private GlobalCoordinate coordinate;

    @Override
    public void fromBytes(ByteBuf buf) {
        int dim = buf.readInt();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        coordinate = new GlobalCoordinate(new Coordinate(x, y, z), dim);
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

    public PacketGetRadiationLevel(GlobalCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public PacketReturnRadiation onMessage(PacketGetRadiationLevel message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        World world = player.worldObj;
        float strength = RadiationMonitorItem.calculateRadiationStrength(world, message.coordinate);
        return new PacketReturnRadiation(strength);
    }
}