package mcjty.deepresonance.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

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
}