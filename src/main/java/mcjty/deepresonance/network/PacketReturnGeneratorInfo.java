package mcjty.deepresonance.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

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

    public PacketReturnGeneratorInfo(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketReturnGeneratorInfo(int id, int energy, int refcount, int rfPerTick) {
        this.id = id;
        this.energy = energy;
        this.refcount = refcount;
        this.rfPerTick = rfPerTick;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ReturnGeneratorInfoHelper.setEnergyLevel(this);
        });
        ctx.setPacketHandled(true);
    }
}