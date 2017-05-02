package mcjty.deepresonance.network;

import elec332.core.world.WorldHelper;
import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.lib.network.NetworkTools;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGetCrystalInfo implements IMessage {
    private BlockPos pos;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
    }

    public PacketGetCrystalInfo() {
    }

    public PacketGetCrystalInfo(BlockPos pos) {
        this.pos = pos;
    }

    public static class Handler implements IMessageHandler<PacketGetCrystalInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketGetCrystalInfo message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetCrystalInfo message, MessageContext ctx) {
            World world = ctx.getServerHandler().player.getEntityWorld();
            TileEntity tileEntity = WorldHelper.getTileAt(world, message.pos);
            if (tileEntity instanceof ResonatingCrystalTileEntity) {
                ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) tileEntity;
                PacketReturnCrystalInfo packet = new PacketReturnCrystalInfo(resonatingCrystalTileEntity.getRfPerTick(), resonatingCrystalTileEntity.getPower());
                DeepResonance.networkHandler.sendTo(packet, ctx.getServerHandler().player);
            }
        }
    }

}