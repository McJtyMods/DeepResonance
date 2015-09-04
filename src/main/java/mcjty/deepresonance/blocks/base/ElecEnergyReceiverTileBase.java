package mcjty.deepresonance.blocks.base;

import elec332.core.baseclasses.tileentity.IInventoryTile;
import elec332.core.main.ElecCore;
import elec332.core.network.IElecCoreNetworkTile;
import elec332.core.util.IRunOnce;
import mcjty.entity.GenericEnergyReceiverTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 12-8-2015.
 */
public abstract class ElecEnergyReceiverTileBase extends GenericEnergyReceiverTileEntity implements IInventoryTile, IElecCoreNetworkTile {

    public ElecEnergyReceiverTileBase(int maxEnergy, int maxReceive) {
        super(maxEnergy, maxReceive);
    }

    /**
     * All code below was copied from from ElecCore, if you want to use this, or just want to view the original code, you can find the original code here:
     * https://github.com/Elecs-Mods/ElecCore/blob/master/src/main/java/elec332/core/baseclasses/tileentity/TileBase.java
     */

    @Override
    public void validate() {
        super.validate();
        ElecCore.tickHandler.registerCall(new IRunOnce() {
            @Override
            public void run() {
                if (getWorldObj().blockExists(xCoord, yCoord, zCoord)) {
                    onTileLoaded();
                }
            }
        });
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
        //if (!isInvalid()) {
        super.onChunkUnload();
        //super.invalidate();
        onTileUnloaded();
        //}
    }

    public void onTileLoaded(){
    }

    public void onTileUnloaded(){
    }

    public void notifyNeighboursOfDataChange(){
        this.markDirty();
        this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, blockType);
    }

    public boolean timeCheck() {
        return this.worldObj.getTotalWorldTime() % 32L == 0L;
    }

    public void onPacketReceivedFromClient(EntityPlayerMP sender, int ID, NBTTagCompound data) {
    }

    @Override
    public Object getGuiClient(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public Container getGuiServer(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public void onDataPacket(int i, NBTTagCompound nbtTagCompound) {

    }
}
