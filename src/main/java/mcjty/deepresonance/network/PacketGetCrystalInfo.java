package mcjty.deepresonance.network;

import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.thirteen.Context;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

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

    public PacketGetCrystalInfo(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketGetCrystalInfo(BlockPos pos) {
        this.pos = pos;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            World world = ctx.getSender().getEntityWorld();
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof ResonatingCrystalTileEntity) {
                ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) tileEntity;
                PacketReturnCrystalInfo packet = new PacketReturnCrystalInfo(resonatingCrystalTileEntity.getRfPerTick(), resonatingCrystalTileEntity.getPower());
                DRMessages.INSTANCE.sendTo(packet, ctx.getSender());
            }
        });
        ctx.setPacketHandled(true);
    }
}