package mcjty.deepresonance.blocks.crystalizer;

import elec332.core.world.WorldHelper;
import mcjty.container.InventoryHelper;
import mcjty.deepresonance.blocks.base.ElecEnergyReceiverTileBase;
import mcjty.deepresonance.blocks.smelter.SmelterContainer;
import mcjty.deepresonance.blocks.tank.ITankHook;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CrystalizerTileEntity extends ElecEnergyReceiverTileBase implements ITankHook, ISidedInventory {

    private InventoryHelper inventoryHelper = new InventoryHelper(this, CrystalizerContainer.factory, 1);

    public CrystalizerTileEntity() {
        super(900*ConfigMachines.Smelter.rfPerTick, 3*ConfigMachines.Smelter.rfPerTick);
        checkTanks = true;
    }

    private TileTank rclTank;
    private boolean checkTanks;

    @Override
    protected void checkStateServer() {
    }

    private boolean canWork() {
        if (checkTanks) {
            if (checkTanks()) {
                checkTanks = false;
            } else {
                return false;
            }
        }
        return storage.getMaxEnergyStored() >= ConfigMachines.Smelter.rfPerTick;
    }

    private boolean checkTanks(){
        return rclTank != null
                && rclTank.fill(ForgeDirection.UNKNOWN, new FluidStack(DRFluidRegistry.liquidCrystal, ConfigMachines.Smelter.rclPerOre), false) == ConfigMachines.Smelter.rclPerOre;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
    }



    @Override
    public void hook(TileTank tank, ForgeDirection direction) {
        if (direction != ForgeDirection.DOWN) {
            return;
        } else if (rclTank == null){
            if (validRCLTank(tank)){
                rclTank = tank;
            }
        }
        checkTanks = true;
    }

    @Override
    public void unHook(TileTank tank, ForgeDirection direction) {
        if (tilesEqual(rclTank, tank)){
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
        return first != null && second != null && first.myLocation().equals(second.myLocation()) && WorldHelper.getDimID(first.getWorldObj()) == WorldHelper.getDimID(second.getWorldObj());
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { CrystalizerContainer.SLOT_CRYSTAL };
    }

    @Override
    public boolean canInsertItem(int index, ItemStack item, int side) {
        return SmelterContainer.factory.isInputSlot(index) || SmelterContainer.factory.isSpecificItemSlot(index);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack item, int side) {
        return SmelterContainer.factory.isOutputSlot(index);
    }

    @Override
    public int getSizeInventory() {
        return inventoryHelper.getCount();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventoryHelper.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        return inventoryHelper.decrStackSize(index, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventoryHelper.setInventorySlotContents(getInventoryStackLimit(), index, stack);
    }

    @Override
    public String getInventoryName() {
        return "Crystalizer Inventory";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

}
