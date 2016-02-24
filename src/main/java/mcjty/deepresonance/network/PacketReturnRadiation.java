package mcjty.deepresonance.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

    public PacketReturnRadiation(float strength) {
        this.strength = strength;
    }

    public static class Handler implements IMessageHandler<PacketReturnRadiation, IMessage> {
        @Override
        public IMessage onMessage(PacketReturnRadiation message, MessageContext ctx) {
            ReturnRadiationHelper.setRadiationLevel(message);
            return null;
        }

    }
}