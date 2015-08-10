package mcjty.deepresonance.blocks.tank;

import elec332.core.baseclasses.tileentity.TileBase;
import elec332.core.multiblock.dynamic.IDynamicMultiBlockTile;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidAcceptor;
import mcjty.deepresonance.grid.fluid.event.FluidTileEvent;
import mcjty.deepresonance.grid.tank.DRTankMultiBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * Created by Elec332 on 9-8-2015.
 */
public class TileTank extends TileBase implements IDynamicMultiBlockTile<DRTankMultiBlock>, IFluidHandler, IDeepResonanceFluidAcceptor{

    public TileTank(){
        super();
    }

    @Override
    public void onTileLoaded() {
        super.onTileLoaded();
        if (!worldObj.isRemote) {
            DeepResonance.worldGridRegistry.getTankRegistry().get(getWorldObj()).addTile(this);
            MinecraftForge.EVENT_BUS.post(new FluidTileEvent.Load(this));
        }
    }

    @Override
    public void onTileUnloaded() {
        super.onTileUnloaded();
        if (!worldObj.isRemote) {
            DeepResonance.worldGridRegistry.getTankRegistry().get(getWorldObj()).removeTile(this);
            MinecraftForge.EVENT_BUS.post(new FluidTileEvent.Unload(this));
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
            lastSeenFluid = multiBlock.getFluid();
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
    public void setMultiBlock(DRTankMultiBlock multiBlock) {
        this.multiBlock = multiBlock;
    }

    @Override
    public DRTankMultiBlock getMultiBlock() {
        return multiBlock;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return multiBlock == null ? 0 : multiBlock.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return multiBlock == null ? null : multiBlock.drain(from, resource, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
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

    /**
     * @param direction The direction
     * @return Weather the tile can connect to the given side
     */
    @Override
    public boolean canAcceptFrom(ForgeDirection direction) {
        return true;
    }

    /**
     * This gets called right before IDeepResonanceFluidAcceptor#acceptFluid,
     * the network to which the tile is connected will attempt to give you
     * as much RS as returned by this method.
     *
     * @param from from where the fluid will be provided
     * @return the requested amount of RS
     */
    @Override
    public int getRequestedAmount(ForgeDirection from) {
        return multiBlock == null ? 0 : Math.min(multiBlock.getFreeSpace(), 1000);
    }

    /**
     * This gets called right after IDeepResonanceFluidAcceptor#getRequestedAmount,
     * the provided FluidStack will contain less or as much fluid as the value provided
     * in IDeepResonanceFluidAcceptor#getRequestedAmount.
     *
     * @param fluidStack The provided FluidStack
     * @param from       From where the fluid was provided
     * @return The amount of fluid that wasn't used
     */
    @Override
    public FluidStack acceptFluid(FluidStack fluidStack, ForgeDirection from) {
        fill(from, fluidStack, true);
        return null;
    }
}
