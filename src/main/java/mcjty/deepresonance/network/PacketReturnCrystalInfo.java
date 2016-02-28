package mcjty.deepresonance.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

    public static class Handler implements IMessageHandler<PacketReturnCrystalInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketReturnCrystalInfo message, MessageContext ctx) {
            ReturnCrystalInfoHelper.setEnergyLevel(message);
            return null;
        }
    }

}