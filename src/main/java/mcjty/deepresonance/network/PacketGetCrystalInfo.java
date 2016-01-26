package mcjty.deepresonance.network;

import elec332.core.world.WorldHelper;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PacketGetCrystalInfo implements IMessage,IMessageHandler<PacketGetCrystalInfo, PacketReturnCrystalInfo> {
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

    public PacketGetCrystalInfo() {
    }

    public PacketGetCrystalInfo(BlockPos pos){
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public PacketGetCrystalInfo(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public PacketReturnCrystalInfo onMessage(PacketGetCrystalInfo message, MessageContext ctx) {
        World world = ctx.getServerHandler().playerEntity.worldObj;
        TileEntity tileEntity = WorldHelper.getTileAt(world, new BlockPos(x, y, z));
        if (tileEntity instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) tileEntity;
            return new PacketReturnCrystalInfo(resonatingCrystalTileEntity.getRfPerTick(), resonatingCrystalTileEntity.getPower());
        }
        return null;
    }

}