package mcjty.deepresonance.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import net.minecraft.world.World;

public class PacketGetGeneratorInfo implements IMessage,IMessageHandler<PacketGetGeneratorInfo, PacketReturnGeneratorInfo> {
    private int networkId;

    @Override
    public void fromBytes(ByteBuf buf) {
        networkId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(networkId);
    }

    public PacketGetGeneratorInfo() {
    }

    public PacketGetGeneratorInfo(int networkId) {
        this.networkId = networkId;
    }

    @Override
    public PacketReturnGeneratorInfo onMessage(PacketGetGeneratorInfo message, MessageContext ctx) {
        World world = ctx.getServerHandler().playerEntity.worldObj;
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(world);
        DRGeneratorNetwork.Network network = generatorNetwork.getChannel(message.networkId);
        if (network == null) {
            return null;
        }

        return new PacketReturnGeneratorInfo(message.networkId, network.getEnergy(), network.getRefcount());
    }

}