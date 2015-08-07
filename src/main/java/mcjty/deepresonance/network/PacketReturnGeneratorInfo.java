package mcjty.deepresonance.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class PacketReturnGeneratorInfo implements IMessage {
    private int id;
    private int energy;
    private int refcount;

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
        energy = buf.readInt();
        refcount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(energy);
        buf.writeInt(refcount);
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

    public PacketReturnGeneratorInfo() {
    }

    public PacketReturnGeneratorInfo(int id, int energy, int refcount) {
        this.id = id;
        this.energy = energy;
        this.refcount = refcount;
    }
}