package mcjty.deepresonance.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

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

    public PacketReturnCrystalInfo(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketReturnCrystalInfo(int rfPerTick, float power) {
        this.rfPerTick = rfPerTick;
        this.power = power;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ReturnCrystalInfoHelper.setEnergyLevel(this);
        });
        ctx.setPacketHandled(true);
    }
}