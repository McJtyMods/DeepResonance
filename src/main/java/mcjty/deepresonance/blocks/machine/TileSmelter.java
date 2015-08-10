package mcjty.deepresonance.blocks.machine;

import elec332.core.baseclasses.tileentity.TileBase;
import elec332.core.util.BasicInventory;
import elec332.core.util.DirectionHelper;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * Created by Elec332 on 9-8-2015.
 */
public class TileSmelter extends TileBase implements IFluidHandler{

    public TileSmelter(){
        this.lavaTank = new FluidTank(10*FluidContainerRegistry.BUCKET_VOLUME);
        this.rclTank = new FluidTank(10* FluidContainerRegistry.BUCKET_VOLUME);
        this.inventory = new BasicInventory("InventorySmelter", 1, this);
    }

    private FluidTank lavaTank;
    private FluidTank rclTank;
    private BasicInventory inventory;
    private int progress;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (progress == 400){
            if (timeCheck()) {
                if (canWork()) //Prevent too much checking
                    progress--;
            }
        } else if (progress > 0 && canWork()){
            progress--;
        } else {
            if (canWork())
                smelt();
            progress = 400;
        }
    }

    private boolean canWork(){
        return lavaTank.getFluidAmount() > 2.5f*FluidContainerRegistry.BUCKET_VOLUME && inventory.getStackInSlot(0) != null;
    }

    private void smelt(){
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        NBTTagCompound tag = new NBTTagCompound();
        lavaTank.writeToNBT(tag);
        tagCompound.setTag("lava", tag);
        tag = new NBTTagCompound();
        rclTank.writeToNBT(tag);
        tagCompound.setTag("rcl", tag);
        inventory.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        lavaTank.readFromNBT(tagCompound.getCompoundTag("lava"));
        rclTank.readFromNBT(tagCompound.getCompoundTag("rcl"));
        inventory.readFromNBT(tagCompound);
    }

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param from     Orientation the Fluid is pumped in from.
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param doFill   If false, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource != null && canFill(from, resource.getFluid()))
            return lavaTank.fill(resource, doFill);
        return 0;
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param from     Orientation the Fluid is drained to.
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param doDrain  If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource != null && canDrain(from, resource.getFluid()))
            return rclTank.drain(resource.amount, doDrain);
        return null;
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * <p/>
     * This method is not Fluid-sensitive.
     *
     * @param from     Orientation the fluid is drained to.
     * @param maxDrain Maximum amount of fluid to drain.
     * @param doDrain  If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (DirectionHelper.rotateRight(DirectionHelper.getDirectionFromNumber(getBlockMetadata())) == from)
            return rclTank.drain(maxDrain, doDrain);
        return null;
    }

    /**
     * Returns true if the given fluid can be inserted into the given direction.
     * <p/>
     * More formally, this should return true if fluid is able to enter from the given direction.
     *
     * @param from
     * @param fluid
     */
    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return DirectionHelper.rotateLeft(DirectionHelper.getDirectionFromNumber(getBlockMetadata())) == from && fluid != null && fluid == FluidRegistry.LAVA;
    }

    /**
     * Returns true if the given fluid can be extracted from the given direction.
     * <p/>
     * More formally, this should return true if fluid is able to leave from the given direction.
     *
     * @param from
     * @param fluid
     */
    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return DirectionHelper.rotateRight(DirectionHelper.getDirectionFromNumber(getBlockMetadata())) == from && fluid != null && fluid == DRFluidRegistry.liquidCrystal;
    }

    /**
     * Returns an array of objects which represent the internal tanks. These objects cannot be used
     * to manipulate the internal tanks. See {@link net.minecraftforge.fluids.FluidTankInfo}.
     *
     * @param from Orientation determining which tanks should be queried.
     * @return Info for the relevant internal tanks.
     */
    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{
                lavaTank.getInfo(),
                rclTank.getInfo()
        };
    }
}
