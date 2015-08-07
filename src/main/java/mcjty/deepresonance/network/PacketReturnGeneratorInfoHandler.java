package mcjty.deepresonance.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnGeneratorInfoHandler implements IMessageHandler<PacketReturnGeneratorInfo, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnGeneratorInfo message, MessageContext ctx) {
        ReturnGeneratorInfoHelper.setEnergyLevel(message);
        return null;
    }

}