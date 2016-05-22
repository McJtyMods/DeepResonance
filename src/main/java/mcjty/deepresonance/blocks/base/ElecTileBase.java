package mcjty.deepresonance.blocks.base;

import elec332.core.main.ElecCore;
import elec332.core.network.IElecCoreNetworkTile;
import elec332.core.server.ServerHelper;
import elec332.core.world.WorldHelper;
import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 12-8-2015.
 */
public abstract class ElecTileBase extends GenericTileEntity implements IElecCoreNetworkTile {

    @Override
    public void validate() {
        super.validate();
        ElecCore.tickHandler.registerCall(() -> {
            if (WorldHelper.chunkLoaded(worldObj, pos)) {
                onTileLoaded();
            }
        }, worldObj);
    }

    @Override
    public void invalidate() {
        if (!isInvalid()){
            super.invalidate();
            onTileUnloaded();
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        onTileUnloaded();
    }

    public void onTileLoaded(){
    }

    public void onTileUnloaded(){
    }

    public void notifyNeighboursOfDataChange(){
        this.markDirty();
        this.worldObj.notifyNeighborsOfStateChange(pos, blockType);
    }

    public boolean timeCheck() {
        return this.worldObj.getTotalWorldTime() % 32L == 0L;
    }

    public void syncData() {
        IBlockState state = worldObj.getBlockState(pos);
        this.worldObj.notifyBlockUpdate(this.pos, state, state, 3);
    }

    @Override
    public void onPacketReceivedFromClient(EntityPlayerMP sender, int ID, NBTTagCompound data) {
    }

    public void sendPacket(int ID, NBTTagCompound data) {
        for (EntityPlayerMP player : ServerHelper.instance.getAllPlayersWatchingBlock(this.worldObj, this.pos)) {
            this.sendPacketTo(player, ID, data);
        }
    }

    public void sendPacketTo(EntityPlayerMP player, int ID, NBTTagCompound data) {
        player.connection.sendPacket(new SPacketUpdateTileEntity(this.pos, ID, data));
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(this.pos, 0, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        if(packet.getTileEntityType() == 0) {
            this.readFromNBT(packet.getNbtCompound());
        } else {
            this.onDataPacket(packet.getTileEntityType(), packet.getNbtCompound());
        }

    }

    @Override
    public void onDataPacket(int id, NBTTagCompound tag) {
    }

}
