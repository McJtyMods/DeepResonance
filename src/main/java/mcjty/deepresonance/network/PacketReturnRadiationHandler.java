package mcjty.deepresonance.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnRadiationHandler implements IMessageHandler<PacketReturnRadiation, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnRadiation message, MessageContext ctx) {
        ReturnRadiationHelper.setRadiationLevel(message);
        return null;
    }

}