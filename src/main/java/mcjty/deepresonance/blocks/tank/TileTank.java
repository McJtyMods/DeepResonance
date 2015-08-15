package mcjty.deepresonance.blocks.tank;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.baseclasses.tileentity.TileBase;
import elec332.core.compat.handlers.WailaCompatHandler;
import elec332.core.main.ElecCore;
import elec332.core.multiblock.dynamic.IDynamicMultiBlockTile;
import elec332.core.server.ServerHelper;
import elec332.core.util.NBTHelper;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidAcceptor;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.grid.fluid.event.FluidTileEvent;
import mcjty.deepresonance.grid.tank.DRTankMultiBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 9-8-2015.
 */
public class TileTank extends TileBase implements IDynamicMultiBlockTile<DRTankMultiBlock>, IFluidHandler, IDeepResonanceFluidAcceptor, IFluidTank, WailaCompatHandler.IWailaInfoTile {

    public TileTank(){
        super();
    }

    @SideOnly(Side.CLIENT)
    public Fluid clientRenderFluid;
    @SideOnly(Side.CLIENT)
    public int totalFluidAmount;
    @SideOnly(Side.CLIENT)
    public float renderHeight; //Value from 0.0f to 1.0f
    private long lastTime;

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
    public void readItemStackNBT(NBTTagCompound tagCompound) {
        super.readItemStackNBT(tagCompound);
        if (tagCompound.hasKey("fluid"))
            this.myTank = FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag("fluid"));
        if (tagCompound.hasKey("lastSeenFluid"))
            this.lastSeenFluid = FluidRegistry.getFluid(tagCompound.getString("lastSeenFluid"));
    }

    @Override
    public void writeToItemStack(NBTTagCompound tagCompound) {
        super.writeToItemStack(tagCompound);
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
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        for (Map.Entry<ITankHook, ForgeDirection> entry : getConnectedHooks().entrySet()){
            entry.getKey().hook(this, entry.getValue());
        }
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
        notifyChanges(doFill);
        return multiBlock == null ? 0 : multiBlock.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        notifyChanges(doDrain);
        return multiBlock == null ? null : multiBlock.drain(from, resource, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        notifyChanges(doDrain);
        return multiBlock == null ? null : multiBlock.drain(from, maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return multiBlock != null && multiBlock.canFill(from, fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return multiBlock != null && multiBlock.canDrain(from, fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return multiBlock == null ? new FluidTankInfo[0] : multiBlock.getTankInfo(from);
    }

    @Override
    public boolean canAcceptFrom(ForgeDirection direction) {
        return true;
    }

    @Override
    public int getRequestedAmount(ForgeDirection from) {
        return multiBlock == null ? 0 : Math.min(multiBlock.getFreeSpace(), 1000);
    }

    @Override
    public FluidStack acceptFluid(FluidStack fluidStack, ForgeDirection from) {
        fill(from, fluidStack, true);
        notifyChanges(true);
        return null;
    }

    @Override
    public FluidStack getFluid() {
        return multiBlock == null ? null : multiBlock.getFluid();
    }

    @Override
    public int getFluidAmount() {
        return multiBlock == null ? 0 : multiBlock.getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return multiBlock == null ? 0 : multiBlock.getCapacity();
    }

    @Override
    public FluidTankInfo getInfo() {
        return multiBlock == null ? null : multiBlock.getInfo();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        notifyChanges(doFill);
        return multiBlock == null ? 0 : multiBlock.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        notifyChanges(doDrain);
        return multiBlock == null ? null : multiBlock.drain(maxDrain, doDrain);
    }

    private void notifyChanges(boolean b){
        if (multiBlock != null && b){
            for (Map.Entry<ITankHook, ForgeDirection> entry : getConnectedHooks().entrySet()){
                entry.getKey().onContentChanged(this, entry.getValue());
            }
        }
    }

    private Map<ITankHook, ForgeDirection> getConnectedHooks(){
        Map<ITankHook, ForgeDirection> ret = Maps.newHashMap();
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS){
            TileEntity tile = WorldHelper.getTileAt(worldObj, myLocation().atSide(direction));
            if (tile instanceof ITankHook)
                ret.put((ITankHook) tile, direction.getOpposite());
        }
        return ret;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        currentTip.add("Fluid: "+ DRFluidRegistry.getFluidName(clientRenderFluid));
        currentTip.add("Amount: "+totalFluidAmount);
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
                sendPacket(2, new NBTHelper().addToTag(getMultiBlock() == null ? 0 : getMultiBlock().getFluidAmount(), "totalFluid").toNBT());
                return;
            case 2:

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
                return;
            case 3:
                this.renderHeight = tag.getFloat("render");
        }
    }
}
