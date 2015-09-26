package mcjty.deepresonance.blocks.valve;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.base.ElecTileBase;
import mcjty.deepresonance.blocks.tank.ITankHook;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ValveTileEntity extends ElecTileBase implements ITankHook {

    public ValveTileEntity() {
    }

    private TileTank bottomTank;
    private TileTank topTank;
    private int progress = 0;


    @Override
    protected void checkStateServer() {
        progress--;
        markDirty();
        if (progress > 0) {
            return;
        }
        progress = ConfigMachines.Valve.ticksPerOperation;

        if (topTank == null || bottomTank == null) {
            return;
        }

        FluidStack fluidStack = topTank.drain(ForgeDirection.UNKNOWN, ConfigMachines.Valve.rclPerOperation, false);
        if (fluidStack != null && fillBottomTank(fluidStack.amount)) {
            fluidStack = topTank.drain(ForgeDirection.UNKNOWN, ConfigMachines.Valve.rclPerOperation, true);
            bottomTank.fill(ForgeDirection.UNKNOWN, fluidStack, true);
        }
    }

    private boolean fillBottomTank(int amount) {
        return bottomTank.fill(ForgeDirection.UNKNOWN, new FluidStack(DRFluidRegistry.liquidCrystal, amount), false) == amount;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("progress", progress);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        progress = tagCompound.getInteger("progress");
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
    }

    @Override
    public void hook(TileTank tank, ForgeDirection direction) {
        if (direction == ForgeDirection.DOWN){
            if (validRCLTank(tank)) {
                bottomTank = tank;
            }
        } else if (topTank == null){
            if (validRCLTank(tank)){
                topTank = tank;
            }
        }
    }

    @Override
    public void unHook(TileTank tank, ForgeDirection direction) {
        if (tilesEqual(bottomTank, tank)){
            bottomTank = null;
            notifyNeighboursOfDataChange();
        } else if (tilesEqual(topTank, tank)){
            topTank = null;
            notifyNeighboursOfDataChange();
        }
    }

    @Override
    public void onContentChanged(TileTank tank, ForgeDirection direction) {
        if (tilesEqual(topTank, tank)){
            if (!validRCLTank(tank)) {
                topTank = null;
            }
        }
        if (tilesEqual(bottomTank, tank)){
            if (!validRCLTank(tank)) {
                bottomTank = null;
            }
        }
    }

    private boolean validRCLTank(TileTank tank){
        Fluid fluid = DRFluidRegistry.getFluidFromStack(tank.getFluid());
        return fluid == null || fluid == DRFluidRegistry.liquidCrystal;
    }

    private boolean tilesEqual(TileTank first, TileTank second){
        return first != null && second != null && first.myLocation().equals(second.myLocation()) && WorldHelper.getDimID(first.getWorldObj()) == WorldHelper.getDimID(second.getWorldObj());
    }
}
