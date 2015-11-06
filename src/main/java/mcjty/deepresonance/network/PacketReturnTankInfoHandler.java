package mcjty.deepresonance.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnTankInfoHandler implements IMessageHandler<PacketReturnTankInfo, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnTankInfo message, MessageContext ctx) {
        ReturnTankInfoHelper.setEnergyLevel(message);
        return null;
    }

}