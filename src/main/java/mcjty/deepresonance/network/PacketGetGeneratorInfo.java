package mcjty.deepresonance.network;

import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.lib.thirteen.Context;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

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

    public PacketGetGeneratorInfo(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketGetGeneratorInfo(int networkId) {
        this.networkId = networkId;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            World world = ctx.getSender().getEntityWorld();
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(world);
            DRGeneratorNetwork.Network network = generatorNetwork.getChannel(networkId);
            if (network == null) {
                return;
            }

            PacketReturnGeneratorInfo packet = new PacketReturnGeneratorInfo(networkId, network.getEnergy(), network.getGeneratorBlocks(), network.getLastRfPerTick());
            DRMessages.INSTANCE.sendTo(packet, ctx.getSender());
        });
        ctx.setPacketHandled(true);
    }
}