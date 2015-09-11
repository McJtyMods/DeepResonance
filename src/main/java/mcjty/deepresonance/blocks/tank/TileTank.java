package mcjty.deepresonance.blocks.tank;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.compat.handlers.WailaCompatHandler;
import elec332.core.multiblock.dynamic.IDynamicMultiBlockTile;
import elec332.core.util.NBTHelper;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidAcceptor;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidProvider;
import mcjty.deepresonance.blocks.base.ElecTileBase;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.deepresonance.grid.fluid.event.FluidTileEvent;
import mcjty.deepresonance.grid.tank.DRTankMultiBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 9-8-2015.
 */
public class TileTank extends ElecTileBase implements IDynamicMultiBlockTile<DRTankMultiBlock>, IFluidHandler, IDeepResonanceFluidAcceptor, IDeepResonanceFluidProvider, WailaCompatHandler.IWailaInfoTile {

    public static final int SETTING_NONE = 0;
    public static final int SETTING_ACCEPT = 1;
    public static final int SETTING_PROVIDE = 2;
    public static final int SETTING_MAX = 2;

    public TileTank(){
        super();
        this.settings = Maps.newHashMap();
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS){
            settings.put(direction, SETTING_NONE);
        }
    }

    @SideOnly(Side.CLIENT)
    private Fluid clientRenderFluid;
    @SideOnly(Side.CLIENT)
    private int totalFluidAmount;
    @SideOnly(Side.CLIENT)
    private int tankCapacity;
    @SideOnly(Side.CLIENT)
    private float renderHeight; //Value from 0.0f to 1.0f
    @SideOnly(Side.CLIENT)
    private LiquidCrystalFluidTagData fluidData;

    private long lastTime;
    protected Map<ForgeDirection, Integer> settings;

    @Override
    public void onTileLoaded() {
        super.onTileLoaded();
        if (!worldObj.isRemote) {
            DeepResonance.worldGridRegistry.getTankRegistry().get(getWorldObj()).addTile(this);
            MinecraftForge.EVENT_BUS.post(new FluidTileEvent.Load(this));
            for (Map.Entry<ITankHook, ForgeDirection> entry : getConnectedHooks().entrySet()){
                entry.getKey().hook(this, entry.getValue());
            }
        }
    }

    @Override
    public void onTileUnloaded() {
        super.onTileUnloaded();
        if (!worldObj.isRemote) {
            DeepResonance.worldGridRegistry.getTankRegistry().get(getWorldObj()).removeTile(this);
            MinecraftForge.EVENT_BUS.post(new FluidTileEvent.Unload(this));
            for (Map.Entry<ITankHook, ForgeDirection> entry : getConnectedHooks().entrySet()){
                entry.getKey().unHook(this, entry.getValue());
            }
        }
    }

    private DRTankMultiBlock multiBlock;
    public FluidStack myTank;
    public Fluid lastSeenFluid;

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        NBTTagList tagList = tagCompound.getTagList("settings", Constants.NBT.TAG_COMPOUND);
        if (tagList != null){
            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound tag = tagList.getCompoundTagAt(i);
                settings.put(ForgeDirection.valueOf(tag.getString("dir")), tag.getInteger("n"));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        NBTTagList tagList = new NBTTagList();
        for (Map.Entry<ForgeDirection, Integer> entry : settings.entrySet()){
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
        if (tagCompound.hasKey("fluid"))
            this.myTank = FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag("fluid"));
        if (tagCompound.hasKey("lastSeenFluid"))
            this.lastSeenFluid = FluidRegistry.getFluid(tagCompound.getString("lastSeenFluid"));
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        if (multiBlock != null) {
            myTank = multiBlock.getFluidShare(this);
            lastSeenFluid = multiBlock.getStoredFluid();
        }
        if (myTank != null) {
            NBTTagCompound fluidTag = new NBTTagCompound();
            myTank.writeToNBT(fluidTag);
            tagCompound.setTag("fluid", fluidTag);
        }
        if (lastSeenFluid != null)
            tagCompound.setString("lastSeenFluid", FluidRegistry.getFluidName(lastSeenFluid));
    }

    @Override
    public boolean canUpdate() {
        return false;
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
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!isInput(from))
            return 0;
        notifyChanges(doFill);
        return multiBlock == null ? 0 : multiBlock.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (!isOutput(from))
            return null;
        notifyChanges(doDrain);
        return multiBlock == null ? null : multiBlock.drain(from, resource, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (!isOutput(from))
            return null;
        notifyChanges(doDrain);
        return multiBlock == null ? null : multiBlock.drain(from, maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return isInput(from) && multiBlock != null && multiBlock.canFill(from, fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return isOutput(from) && multiBlock != null && multiBlock.canDrain(from, fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return multiBlock == null ? new FluidTankInfo[0] : multiBlock.getTankInfo(from);
    }

    @Override
    public boolean canAcceptFrom(ForgeDirection direction) {
        return isInput(direction);
    }

    @Override
    public boolean canProvideTo(ForgeDirection direction) {
        return isOutput(direction);
    }

    @Override
    public FluidStack getProvidedFluid(int maxProvided, ForgeDirection from) {
        if (!isOutput(from))
            return null;
        return getMultiBlock() == null ? null : getMultiBlock().drain(maxProvided, true);
    }

    @Override
    public int getRequestedAmount(ForgeDirection from) {
        if (!isInput(from))
            return 0;
        return multiBlock == null ? 0 : Math.min(multiBlock.getFreeSpace(), 1000);
    }

    @Override
    public FluidStack acceptFluid(FluidStack fluidStack, ForgeDirection from) {
        if (!isInput(from)) {
            fill(from, fluidStack, true);
            notifyChanges(true);
            return null;
        } else {
            return fluidStack;
        }
    }

    @SideOnly(Side.CLIENT)
    public Fluid getClientRenderFluid() {
        return clientRenderFluid;
    }

    @SideOnly(Side.CLIENT)
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
            for (Map.Entry<ITankHook, ForgeDirection> entry : getConnectedHooks().entrySet()){
                entry.getKey().onContentChanged(this, entry.getValue());
            }
        }
    }

    protected Map<ITankHook, ForgeDirection> getConnectedHooks(){
        Map<ITankHook, ForgeDirection> ret = Maps.newHashMap();
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS){
            TileEntity tile = WorldHelper.getTileAt(worldObj, myLocation().atSide(direction));
            if (tile instanceof ITankHook)
                ret.put((ITankHook) tile, direction.getOpposite());
        }
        return ret;
    }

    public boolean isInput(ForgeDirection direction){
        return direction == ForgeDirection.UNKNOWN || settings.get(direction) == SETTING_ACCEPT;
    }

    public boolean isOutput(ForgeDirection direction){
        return direction == ForgeDirection.UNKNOWN || settings.get(direction) == SETTING_PROVIDE;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        int i = settings.get(accessor.getSide());
        currentTip.add("Mode: "+(i == SETTING_NONE ? "none" : (i == SETTING_ACCEPT ? "accept" : "provide")));
        currentTip.add("Fluid: "+ DRFluidRegistry.getFluidName(clientRenderFluid));
        currentTip.add("Amount: "+totalFluidAmount + " (" + tankCapacity + ")");
        if (fluidData != null) {
            currentTip.add(EnumChatFormatting.YELLOW + "Quality: " + (int)(fluidData.getQuality() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Purity: " + (int)(fluidData.getPurity() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Power: " + (int)(fluidData.getPower() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Time: " + (int)(fluidData.getLastingTime() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Crystalizing: " + (int)(fluidData.getCrystallisingTime() * 100) + "%");
        }
        if (System.currentTimeMillis() - lastTime > 100){
            lastTime = System.currentTimeMillis();
            sendPacketToServer(1, new NBTTagCompound());
        }
        return currentTip;
    }

    @Override
    public void onPacketReceivedFromClient(EntityPlayerMP sender, int ID, NBTTagCompound data) {
        switch (ID){
            case 1:
                sendPacket(2, new NBTHelper()
                        .addToTag(getFluidAmount(), "totalFluid")
                        .addToTag(getCapacity(), "capacity")
                        .addToTag(getFluidTag(), "fluidTag")
                        .toNBT());
        }
    }

    @Override
    public void onDataPacket(int id, NBTTagCompound tag) {
        switch (id){
            case 1:
                this.clientRenderFluid = FluidRegistry.getFluid(tag.getString("fluid"));
                return;
            case 2:
                this.totalFluidAmount = tag.getInteger("totalFluid");
                this.tankCapacity = tag.getInteger("capacity");
                NBTTagCompound fluidTag = (NBTTagCompound) tag.getTag("fluidTag");
                fluidData = LiquidCrystalFluidTagData.fromNBT(fluidTag);
                return;
            case 3:
                this.renderHeight = tag.getFloat("render");
        }
    }
}
