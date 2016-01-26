package mcjty.deepresonance.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnGeneratorInfoHandler implements IMessageHandler<PacketReturnGeneratorInfo, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnGeneratorInfo message, MessageContext ctx) {
        ReturnGeneratorInfoHelper.setEnergyLevel(message);
        return null;
    }

}