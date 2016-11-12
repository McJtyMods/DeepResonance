package mcjty.deepresonance.network;

import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGetGeneratorInfo implements IMessage {
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


    public static class Handler implements IMessageHandler<PacketGetGeneratorInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketGetGeneratorInfo message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetGeneratorInfo message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.worldObj;
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(world);
            DRGeneratorNetwork.Network network = generatorNetwork.getChannel(message.networkId);
            if (network == null) {
                return;
            }

            PacketReturnGeneratorInfo packet = new PacketReturnGeneratorInfo(message.networkId, network.getEnergy(), network.getGeneratorBlocks(), network.getLastRfPerTick());
            DeepResonance.networkHandler.sendTo(packet, ctx.getServerHandler().playerEntity);
        }
    }
}