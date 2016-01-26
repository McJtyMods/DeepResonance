package mcjty.deepresonance.blocks.tank;

import com.google.common.collect.Maps;
import elec332.core.api.annotations.RegisterTile;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import elec332.core.multiblock.dynamic.IDynamicMultiBlockTile;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidAcceptor;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidProvider;
import mcjty.deepresonance.blocks.base.ElecTileBase;
import mcjty.deepresonance.grid.fluid.event.FluidTileEvent;
import mcjty.deepresonance.grid.tank.DRTankMultiBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;

import java.util.Map;

/**
 * Created by Elec332 on 9-8-2015.
 */
@RegisterTile(name = "DeepResonanceTankTileEntity")
public class TileTank extends ElecTileBase implements IDynamicMultiBlockTile<DRTankMultiBlock>, IFluidHandler, IDeepResonanceFluidAcceptor, IDeepResonanceFluidProvider {

    public static final int SETTING_NONE = 0;
    public static final int SETTING_ACCEPT = 1;
    public static final int SETTING_PROVIDE = 2;
    public static final int SETTING_MAX = 2;

    public TileTank(){
        super();
        this.settings = Maps.newHashMap();
        for (EnumFacing direction : EnumFacing.VALUES){
            settings.put(direction, SETTING_NONE);
        }
        this.multiBlockSaveData = new NBTTagCompound();
    }

    private Fluid clientRenderFluid;

    // Client only
    private float renderHeight; //Value from 0.0f to 1.0f

    private NBTTagCompound multiBlockSaveData;

    protected Map<EnumFacing, Integer> settings;

    @Override
    public void onTileLoaded() {
        super.onTileLoaded();
        if (!worldObj.isRemote) {
            DeepResonance.worldGridRegistry.getTankRegistry().get(worldObj).addTile(this);
            MinecraftForge.EVENT_BUS.post(new FluidTileEvent.Load(this));
            for (Map.Entry<ITankHook, EnumFacing> entry : getConnectedHooks().entrySet()){
                entry.getKey().hook(this, entry.getValue());
            }
        }
    }

    @Override
    public void onTileUnloaded() {
        super.onTileUnloaded();
        if (!worldObj.isRemote) {
            DeepResonance.worldGridRegistry.getTankRegistry().get(worldObj).removeTile(this);
            MinecraftForge.EVENT_BUS.post(new FluidTileEvent.Unload(this));
            for (Map.Entry<ITankHook, EnumFacing> entry : getConnectedHooks().entrySet()){
                entry.getKey().unHook(this, entry.getValue());
            }
        }
    }

    private DRTankMultiBlock multiBlock;
    public FluidStack myTank;
    public Fluid lastSeenFluid;

    public Map<EnumFacing, Integer> getSettings() {
        return settings;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        NBTTagList tagList = tagCompound.getTagList("settings", Constants.NBT.TAG_COMPOUND);
        if (tagList != null){
            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound tag = tagList.getCompoundTagAt(i);
                settings.put(EnumFacing.valueOf(tag.getString("dir")), tag.getInteger("n"));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        NBTTagList tagList = new NBTTagList();
        for (Map.Entry<EnumFacing, Integer> entry : settings.entrySet()){
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("dir", entry.getKey().toString());
            tag.setInteger("n", entry.getValue());
            tagList.appendTag(tag);
        }
        tagCompound.setTag("settings", tagList);
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
        if (multiBlock != null)
            getMultiBlock().setDataToTile(this);
        tagCompound.setTag("multiBlockData", multiBlockSaveData);
        /*if (multiBlock != null) {
            myTank = multiBlock.getFluidShare(this);
            lastSeenFluid = multiBlock.getStoredFluid();
        }
        if (myTank != null) {
            NBTTagCompound fluidTag = new NBTTagCompound();
            myTank.writeToNBT(fluidTag);
            tagCompound.setTag("fluid", fluidTag);
        }
        if (lastSeenFluid != null)
            tagCompound.setString("lastSeenFluid", FluidRegistry.getFluidName(lastSeenFluid));*/
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
            for (Map.Entry<ITankHook, EnumFacing> entry : getConnectedHooks().entrySet()){
                entry.getKey().onContentChanged(this, entry.getValue());
            }
        }
    }

    protected Map<ITankHook, EnumFacing> getConnectedHooks(){
        Map<ITankHook, EnumFacing> ret = Maps.newHashMap();
        for (EnumFacing direction : EnumFacing.VALUES){
            try {
                TileEntity tile = WorldHelper.getTileAt(worldObj, getPos().offset(direction));
                if (tile instanceof ITankHook)
                    ret.put((ITankHook) tile, direction.getOpposite());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return ret;
    }

    public boolean isInput(EnumFacing direction){
        return direction == null || settings.get(direction) == SETTING_ACCEPT;
    }

    public boolean isOutput(EnumFacing direction){
        return direction == null || settings.get(direction) == SETTING_PROVIDE;
    }

    @Override
    public void onDataPacket(int id, NBTTagCompound tag) {
        switch (id){
            case 1:
                this.clientRenderFluid = FluidRegistry.getFluid(tag.getString("fluid"));
                return;
            case 2:
                return;
            case 3:
                this.renderHeight = tag.getFloat("render");
        }
    }
}
