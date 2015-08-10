package mcjty.deepresonance.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnCrystalInfoHandler implements IMessageHandler<PacketReturnCrystalInfo, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnCrystalInfo message, MessageContext ctx) {
        ReturnCrystalInfoHelper.setEnergyLevel(message);
        return null;
    }

}