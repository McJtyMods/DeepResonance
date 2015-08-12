package mcjty.deepresonance.blocks.machine;

import cofh.api.energy.EnergyStorage;
import elec332.core.util.BasicInventory;
import elec332.core.util.DirectionHelper;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.base.TileEnergyReceiver;
import mcjty.deepresonance.blocks.tank.ITankHook;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * Created by Elec332 on 9-8-2015.
 */
public class TileSmelter extends TileEnergyReceiver implements ITankHook{

    public TileSmelter(){
        super(new EnergyStorage(900*ConfigMachines.Smelter.rfPerTick, 3*ConfigMachines.Smelter.rfPerTick));
        this.inventory = new BasicInventory("InventorySmelter", 1, this);
    }

    private BasicInventory inventory;
    private int progress;
    private TileTank lavaTank;
    private TileTank rclTank;
    private boolean checkTanks;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (progress == 400){
            if (timeCheck()) {
                if (canWork()) { //Prevent too much checking
                    progress--;
                    energyStorage.extractEnergy(ConfigMachines.Smelter.rfPerTick, true);
                }
            }
        } else if (progress > 0 && canWork()){
            progress--;
            energyStorage.extractEnergy(ConfigMachines.Smelter.rfPerTick, true);
        } else {
            if (canWork())
                smelt();
            progress = 400;
        }
    }

    private boolean canWork(){
        if (checkTanks){
            if (checkTanks()) {
                checkTanks = false;
            } else {
                return false;
            }
        }
        return energyStorage.getMaxEnergyStored() >= ConfigMachines.Smelter.rfPerTick && validSlot();
    }

    private boolean checkTanks(){
        return DRFluidRegistry.getFluidFromStack(lavaTank.getFluid()) == FluidRegistry.LAVA && lavaTank.getFluidAmount() > lavaTank.getCapacity()*0.25f && rclTank.fill(new FluidStack(DRFluidRegistry.liquidCrystal, ConfigMachines.Smelter.rclPerOre), false) == ConfigMachines.Smelter.rclPerOre;
    }

    private boolean validSlot(){
        return inventory.getStackInSlot(0) != null && inventory.getStackInSlot(0).getItem() == Item.getItemFromBlock(ModBlocks.resonatingOreBlock);
    }


    private void smelt(){
        inventory.decrStackSize(0, 1);
        ((IFluidHandler)WorldHelper.getTileAt(worldObj, myLocation().atSide(ForgeDirection.UP))).drain(ForgeDirection.DOWN, new FluidStack(FluidRegistry.LAVA, ConfigMachines.Smelter.lavaCost), true);
        ((IFluidHandler)WorldHelper.getTileAt(worldObj, myLocation().atSide(ForgeDirection.DOWN))).fill(ForgeDirection.UP, new FluidStack(DRFluidRegistry.liquidCrystal, ConfigMachines.Smelter.rclPerOre), true);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        inventory.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        inventory.readFromNBT(tagCompound);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return DirectionHelper.getDirectionFromNumber(getBlockMetadata()).getOpposite().equals(from);
    }

    @Override
    public void hook(TileTank tank, ForgeDirection direction) {
        if (direction == ForgeDirection.DOWN){
            this.lavaTank = tank;
        } else if (rclTank != null){
            if (validRCLTank(tank)){
                rclTank = tank;
            }
        }
        checkTanks = true;
    }

    @Override
    public void unHook(TileTank tank, ForgeDirection direction) {
        if (tilesEqual(lavaTank, tank)){
            lavaTank = null;
        } else if (tilesEqual(rclTank, tank)){
            rclTank = null;
            notifyNeighboursOfDataChange();
        }
        checkTanks = true;
    }

    @Override
    public void onContentChanged(TileTank tank, ForgeDirection direction) {
        if (tilesEqual(rclTank, tank)){
            if (!validRCLTank(tank)) {
                rclTank = null;
            }
        }
        checkTanks = true;
    }

    private boolean validRCLTank(TileTank tank){
        Fluid fluid = DRFluidRegistry.getFluidFromStack(tank.getFluid());
        return fluid == null || fluid == DRFluidRegistry.liquidCrystal;
    }

    private boolean tilesEqual(TileTank first, TileTank second){
        return first.myLocation().equals(second.myLocation()) && WorldHelper.getDimID(first.getWorldObj()) == WorldHelper.getDimID(second.getWorldObj());
    }
}
