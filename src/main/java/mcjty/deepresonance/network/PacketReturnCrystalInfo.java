package mcjty.deepresonance.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class PacketReturnCrystalInfo implements IMessage {
    private int rfPerTick;
    private float power;

    @Override
    public void fromBytes(ByteBuf buf) {
        rfPerTick = buf.readInt();
        power = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rfPerTick);
        buf.writeFloat(power);
    }

    public float getPower() {
        return power;
    }

    public int getRfPerTick() {
        return rfPerTick;
    }

    public PacketReturnCrystalInfo() {
    }

    public PacketReturnCrystalInfo(int rfPerTick, float power) {
        this.rfPerTick = rfPerTick;
        this.power = power;
    }
}