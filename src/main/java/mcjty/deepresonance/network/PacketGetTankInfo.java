package mcjty.deepresonance.network;

import elec332.core.world.WorldHelper;
import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.lib.network.NetworkTools;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGetTankInfo implements IMessage {
    private BlockPos pos;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
    }

    public PacketGetTankInfo() {
    }

    public PacketGetTankInfo(BlockPos pos){
        this.pos = pos;
    }

    public static class Handler implements IMessageHandler<PacketGetTankInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketGetTankInfo message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetTankInfo message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.getEntityWorld();
            TileEntity tileEntity = WorldHelper.getTileAt(world, message.pos);
            if (tileEntity instanceof TileTank) {
                TileTank tileTank = (TileTank) tileEntity;
                PacketReturnTankInfo packet = new PacketReturnTankInfo(tileTank.getFluidAmount(), tileTank.getCapacity(), DRFluidRegistry.getFluidName(tileTank.getFluid()), tileTank.getFluidTag());
                DeepResonance.networkHandler.sendTo(packet, ctx.getServerHandler().playerEntity);
            }
        }

    }
}