package mcjty.deepresonance.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PacketGetTankInfo implements IMessage,IMessageHandler<PacketGetTankInfo, PacketReturnTankInfo> {
    private int x;
    private int y;
    private int z;

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public PacketGetTankInfo() {
    }

    public PacketGetTankInfo(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public PacketReturnTankInfo onMessage(PacketGetTankInfo message, MessageContext ctx) {
        World world = ctx.getServerHandler().playerEntity.worldObj;
        TileEntity tileEntity = world.getTileEntity(message.x, message.y, message.z);
        if (tileEntity instanceof TileTank) {
            TileTank tileTank = (TileTank) tileEntity;
            return new PacketReturnTankInfo(tileTank.getFluidAmount(), tileTank.getCapacity(), DRFluidRegistry.getFluidName(tileTank.getFluid()), tileTank.getFluidTag());
        }
        return null;
    }

}