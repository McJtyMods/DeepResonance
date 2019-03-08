package mcjty.deepresonance.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

public class PacketReturnRadiation implements IMessage {
    private float strength;

    @Override
    public void fromBytes(ByteBuf buf) {
        strength = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(strength);
    }

    public float getStrength() {
        return strength;
    }

    public PacketReturnRadiation() {
    }

    public PacketReturnRadiation(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketReturnRadiation(float strength) {
        this.strength = strength;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ReturnRadiationHelper.setRadiationLevel(this);
        });
        ctx.setPacketHandled(true);
    }
}