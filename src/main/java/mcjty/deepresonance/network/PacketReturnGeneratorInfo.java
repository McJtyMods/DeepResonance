package mcjty.deepresonance.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnGeneratorInfo implements IMessage {
    private int id;
    private int energy;
    private int refcount;
    private int rfPerTick;

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
        energy = buf.readInt();
        refcount = buf.readInt();
        rfPerTick = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(energy);
        buf.writeInt(refcount);
        buf.writeInt(rfPerTick);
    }

    public int getId() {
        return id;
    }

    public int getEnergy() {
        return energy;
    }

    public int getRefcount() {
        return refcount;
    }

    public int getRfPerTick() {
        return rfPerTick;
    }

    public PacketReturnGeneratorInfo() {
    }

    public PacketReturnGeneratorInfo(int id, int energy, int refcount, int rfPerTick) {
        this.id = id;
        this.energy = energy;
        this.refcount = refcount;
        this.rfPerTick = rfPerTick;
    }

    public static class Handler implements IMessageHandler<PacketReturnGeneratorInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketReturnGeneratorInfo message, MessageContext ctx) {
            ReturnGeneratorInfoHelper.setEnergyLevel(message);
            return null;
        }

    }
}