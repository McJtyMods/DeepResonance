package mcjty.deepresonance.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnTankInfoHandler implements IMessageHandler<PacketReturnTankInfo, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnTankInfo message, MessageContext ctx) {
        ReturnTankInfoHelper.setEnergyLevel(message);
        return null;
    }

}