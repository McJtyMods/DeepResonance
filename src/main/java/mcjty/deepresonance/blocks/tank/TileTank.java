package mcjty.deepresonance.blocks.tank;

import com.google.common.collect.Maps;
import elec332.core.main.ElecCore;
import elec332.core.multiblock.dynamic.IDynamicMultiBlockTile;
import elec332.core.network.IElecCoreNetworkTile;
import elec332.core.server.ServerHelper;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidAcceptor;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidProvider;
import mcjty.deepresonance.grid.tank.DRTankMultiBlock;
import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.*;

import java.util.Map;

/**
 * Created by Elec332 on 9-8-2015.
 */
public class TileTank extends GenericTileEntity implements IDynamicMultiBlockTile<DRTankMultiBlock>, IFluidHandler, IDeepResonanceFluidAcceptor, IDeepResonanceFluidProvider, IElecCoreNetworkTile {

    public TileTank(){
        super();
        this.settings = Maps.newHashMap();
        for (EnumFacing direction : EnumFacing.VALUES){
            settings.put(direction, Mode.SETTING_NONE);
        }
        this.multiBlockSaveData = new NBTTagCompound();
        this.tankHooks = Maps.newHashMap();
    }

    // Client only
    private Fluid clientRenderFluid;
    private float renderHeight; //Value from 0.0f to 1.0f

    private NBTTagCompound multiBlockSaveData;

    protected Map<EnumFacing, Mode> settings;
    private Map<EnumFacing, ITankHook> tankHooks;

    public static enum Mode implements IStringSerializable {
        SETTING_NONE("none"),
        SETTING_ACCEPT("accept"),   // Blue
        SETTING_PROVIDE("provide"); // Yellow

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Override
    public void onPacketReceivedFromClient(EntityPlayerMP sender, int ID, NBTTagCompound data) {
    }

    public void sendPacket(int ID, NBTTagCompound data) {
        for (EntityPlayerMP player : ServerHelper.instance.getAllPlayersWatchingBlock(this.worldObj, this.pos)) {
            player.connection.sendPacket(new SPacketUpdateTileEntity(this.pos, ID, data));
        }
    }

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

    public void onTileLoaded() {
        if (!worldObj.isRemote) {
            DeepResonance.worldGridRegistry.getTankRegistry().get(worldObj).addTile(this);
            //MinecraftForge.EVENT_BUS.post(new FluidTileEvent.Load(this));
            initHooks();
        }
    }

    public void onTileUnloaded() {
        if (!worldObj.isRemote) {
            DeepResonance.worldGridRegistry.getTankRegistry().get(worldObj).removeTile(this);
            //MinecraftForge.EVENT_BUS.post(new FluidTileEvent.Unload(this));
            for (Map.Entry<EnumFacing, ITankHook> entry : getConnectedHooks().entrySet()){
                entry.getValue().unHook(this, entry.getKey().getOpposite());
            }
            getConnectedHooks().clear();
        }
    }

    public void onNeighborChange(){
        Map<EnumFacing, ITankHook> hookMap = getConnectedHooks();
        for (EnumFacing facing : EnumFacing.VALUES){
            ITankHook tankHook = hookMap.get(facing);
            BlockPos pos = getPos().offset(facing);
            TileEntity tile = WorldHelper.chunkLoaded(worldObj, pos) ? WorldHelper.getTileAt(worldObj, pos) : null;
            if ((tile == null && tankHook != null) || (tile != null && tankHook == null) || (tile != tankHook)){
                hookMap.remove(facing);
                if (tile instanceof ITankHook){
                    ((ITankHook) tile).hook(this, facing.getOpposite());
                    hookMap.put(facing, (ITankHook) tile);
                }
            } else if (tankHook != null){
                tankHook.onContentChanged(this, facing.getOpposite());
            }
        }
    }

    private void initHooks(){
        tankHooks.clear();
        for (EnumFacing facing : EnumFacing.VALUES){
            BlockPos pos = getPos().offset(facing);
            TileEntity tile = WorldHelper.chunkLoaded(worldObj, pos) ? WorldHelper.getTileAt(worldObj, pos) : null;
            if (tile instanceof ITankHook){
                tankHooks.put(facing, (ITankHook) tile);
                ((ITankHook) tile).hook(this, facing.getOpposite());
            }
        }
    }

    private DRTankMultiBlock multiBlock;
    public FluidStack myTank;
    public Fluid lastSeenFluid;

    public Map<EnumFacing, Mode> getSettings() {
        return settings;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        NBTTagList tagList = tagCompound.getTagList("settings", Constants.NBT.TAG_COMPOUND);
        if (tagList != null){
            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound tag = tagList.getCompoundTagAt(i);
                EnumFacing side = EnumFacing.values()[tag.getInteger("dir")];
                Mode mode = Mode.values()[tag.getInteger("n")];
                settings.put(side, mode);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        NBTTagList tagList = new NBTTagList();
        for (Map.Entry<EnumFacing, Mode> entry : settings.entrySet()){
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dir", entry.getKey().ordinal());
            tag.setInteger("n", entry.getValue().ordinal());
            tagList.appendTag(tag);
        }
        tagCompound.setTag("settings", tagList);
        return tagCompound;
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        this.myTank = getFluidStackFromNBT(tagCompound);

        multiBlockSaveData = tagCompound.getCompoundTag("multiBlockData");
        if (tagCompound.hasKey("lastSeenFluid")) { /* legacy compat */
            this.lastSeenFluid = FluidRegistry.getFluid(tagCompound.getString("lastSeenFluid"));
        } else if (multiBlockSaveData.hasKey("lastSeenFluid")){
            this.lastSeenFluid = FluidRegistry.getFluid(multiBlockSaveData.getString("lastSeenFluid"));
        }
    }

    public static FluidStack getFluidStackFromNBT(NBTTagCompound tagCompound) {
        NBTTagCompound mbTag = tagCompound.getCompoundTag("multiBlockData");
        FluidStack s;
        if (tagCompound.hasKey("fluid")) { /* legacy compat */
            s = FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag("fluid"));
        } else if (mbTag.hasKey("fluid")){
            s = FluidStack.loadFluidStackFromNBT(mbTag.getCompoundTag("fluid"));
        } else {
            s = null;
        }
        return s;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        if (multiBlock != null) {
            getMultiBlock().setDataToTile(this);
        }
        ElecCore.systemPrintDebug("Writing restorable NBT @ " + pos);
        tagCompound.setTag("multiBlockData", multiBlockSaveData);
    }

    @Override
    public void setMultiBlock(DRTankMultiBlock multiBlock) {
        this.multiBlock = multiBlock;
    }

    @Override
    public DRTankMultiBlock getMultiBlock() {
        return multiBlock;
    }

    @Override
    public void setSaveData(NBTTagCompound nbtTagCompound) {
        ElecCore.systemPrintDebug("Setting MB save data @ " + pos);
        this.multiBlockSaveData = nbtTagCompound;
    }

    @Override
    public NBTTagCompound getSaveData() {
        return this.multiBlockSaveData;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (!isInput(from))
            return 0;
        notifyChanges(doFill);
        return multiBlock == null ? 0 : multiBlock.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (!isOutput(from))
            return null;
        notifyChanges(doDrain);
        return multiBlock == null ? null : multiBlock.drain(from, resource, doDrain);
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        if (!isOutput(from))
            return null;
        notifyChanges(doDrain);
        return multiBlock == null ? null : multiBlock.drain(from, maxDrain, doDrain);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return isInput(from) && multiBlock != null && multiBlock.canFill(from, fluid);
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return isOutput(from) && multiBlock != null && multiBlock.canDrain(from, fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return multiBlock == null ? new FluidTankInfo[0] : multiBlock.getTankInfo(from);
    }

    @Override
    public boolean canAcceptFrom(EnumFacing direction) {
        return isInput(direction);
    }

    @Override
    public boolean canProvideTo(EnumFacing direction) {
        return isOutput(direction);
    }

    @Override
    public FluidStack getProvidedFluid(int maxProvided, EnumFacing from) {
        if (!isOutput(from))
            return null;
        return getMultiBlock() == null ? null : getMultiBlock().drain(maxProvided, true);
    }

    @Override
    public int getRequestedAmount(EnumFacing from) {
        if (!isInput(from))
            return 0;
        return multiBlock == null ? 0 : Math.min(multiBlock.getFreeSpace(), 1000);
    }

    @Override
    public FluidStack acceptFluid(FluidStack fluidStack, EnumFacing from) {
        if (!isInput(from)) {
            fill(from, fluidStack, true);
            notifyChanges(true);
            return null;
        } else {
            return fluidStack;
        }
    }

    public Fluid getClientRenderFluid() {
        return clientRenderFluid;
    }

    // Client only
    public float getRenderHeight() {
        return renderHeight;
    }

    public FluidStack getFluid() {
        return multiBlock == null ? null : multiBlock.getFluid();
    }

    public NBTTagCompound getFluidTag() {
        return getFluid() == null ? null : getFluid().tag;
    }

    public int getFluidAmount() {
        return multiBlock == null ? 0 : multiBlock.getFluidAmount();
    }

    public int getCapacity() {
        return multiBlock == null ? 0 : multiBlock.getCapacity();
    }

    private void notifyChanges(boolean b){
        if (multiBlock != null && b){
            for (Map.Entry<EnumFacing, ITankHook> entry : getConnectedHooks().entrySet()){
                entry.getValue().onContentChanged(this, entry.getKey().getOpposite());
            }
        }
    }

    private Map<EnumFacing, ITankHook> getConnectedHooks(){
        /*Map<ITankHook, EnumFacing> ret = Maps.newHashMap();
        for (EnumFacing direction : EnumFacing.VALUES){
            try {
                TileEntity tile = WorldHelper.getTileAt(worldObj, getPos().offset(direction));
                if (tile instanceof ITankHook)
                    ret.put((ITankHook) tile, direction.getOpposite());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return ret;*/
        return tankHooks;
    }

    public boolean isInput(EnumFacing direction){
        return direction == null || settings.get(direction) == Mode.SETTING_ACCEPT;
    }

    public boolean isOutput(EnumFacing direction){
        return direction == null || settings.get(direction) == Mode.SETTING_PROVIDE;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return super.getUpdateTag();
    }

    @Override
    public void readClientDataFromNBT(NBTTagCompound tagCompound) {
        super.readClientDataFromNBT(tagCompound);
        this.clientRenderFluid = FluidRegistry.getFluid(tagCompound.getString("fluid"));
        this.renderHeight = tagCompound.getFloat("render");
    }

    public static final int ID_GENERIC = 1;
    public static final int ID_SETFLUID = 2;
    public static final int ID_SETHEIGHT = 3;

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        if (packet.getTileEntityType() == ID_GENERIC) {
            super.onDataPacket(net, packet);
        } else {
            this.onDataPacket(packet.getTileEntityType(), packet.getNbtCompound());
        }
    }

    @Override
    public void onDataPacket(int id, NBTTagCompound tag) {
        switch (id){
            case ID_SETFLUID:
                this.clientRenderFluid = FluidRegistry.getFluid(tag.getString("fluid"));
                return;
            case ID_SETHEIGHT:
                this.renderHeight = tag.getFloat("render");
        }
    }
}
