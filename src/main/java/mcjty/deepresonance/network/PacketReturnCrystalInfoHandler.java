package mcjty.deepresonance.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnCrystalInfoHandler implements IMessageHandler<PacketReturnCrystalInfo, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnCrystalInfo message, MessageContext ctx) {
        ReturnCrystalInfoHelper.setEnergyLevel(message);
        return null;
    }

}