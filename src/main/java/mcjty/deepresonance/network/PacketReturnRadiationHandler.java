package mcjty.deepresonance.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnRadiationHandler implements IMessageHandler<PacketReturnRadiation, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnRadiation message, MessageContext ctx) {
        ReturnRadiationHelper.setRadiationLevel(message);
        return null;
    }

}