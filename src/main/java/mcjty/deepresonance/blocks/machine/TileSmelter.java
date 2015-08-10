package mcjty.deepresonance.blocks.machine;

import cofh.api.energy.EnergyStorage;
import elec332.core.util.BasicInventory;
import elec332.core.util.DirectionHelper;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.base.TileEnergyReceiver;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * Created by Elec332 on 9-8-2015.
 */
public class TileSmelter extends TileEnergyReceiver{

    public TileSmelter(){
        super(new EnergyStorage(900*ConfigMachines.Smelter.rfPerTick, 3*ConfigMachines.Smelter.rfPerTick));
        this.inventory = new BasicInventory("InventorySmelter", 1, this);
    }

    private BasicInventory inventory;
    private int progress;

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
        TileEntity above = WorldHelper.getTileAt(worldObj, myLocation().atSide(ForgeDirection.UP));
        TileEntity below = WorldHelper.getTileAt(worldObj, myLocation().atSide(ForgeDirection.DOWN));
        return above instanceof IFluidHandler && ((IFluidHandler) above).drain(ForgeDirection.DOWN, new FluidStack(FluidRegistry.LAVA, ConfigMachines.Smelter.lavaCost), false).amount == ConfigMachines.Smelter.lavaCost && below instanceof IFluidHandler && ((IFluidHandler) below).fill(ForgeDirection.UP, new FluidStack(DRFluidRegistry.liquidCrystal, ConfigMachines.Smelter.rclPerOre), false) == ConfigMachines.Smelter.rclPerOre && energyStorage.getMaxEnergyStored() >= ConfigMachines.Smelter.rfPerTick && inventory.getStackInSlot(0) != null && inventory.getStackInSlot(0).getItem() == Item.getItemFromBlock(ModBlocks.resonatingOreBlock);
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
}
