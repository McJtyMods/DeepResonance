package mcjty.deepresonance.blocks.purifier;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.base.ElecTileBase;
import mcjty.deepresonance.blocks.tank.ITankHook;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.deepresonance.items.ModItems;
import mcjty.deepresonance.varia.InventoryLocator;
import mcjty.lib.container.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class PurifierTileEntity extends ElecTileBase implements ITankHook, ISidedInventory {

    private InventoryHelper inventoryHelper = new InventoryHelper(this, PurifierContainer.factory, 1);

    public PurifierTileEntity() {
    }

    private TileTank bottomTank;
    private TileTank topTank;
    private int progress = 0;

    // Cache for the inventory used to put the spent filter material in.
    private InventoryLocator inventoryLocator = new InventoryLocator();

    private LiquidCrystalFluidTagData fluidData = null;

    @Override
    protected void checkStateServer() {
        if (progress > 0) {
            progress--;
            if (progress == 0) {
                if (fluidData != null) {
                    // Done. First check if we can actually insert the liquid. If not we postpone this.
                    progress = 1;
                    if (getOutputTank() != null) {
                        if (fillOutputTank()) {
                            doPurify();
                            progress = 0;   // Really done
                        }
                    }
                }
            }
            markDirty();
        } else {
            if (canWork() && validSlot()) {
                progress = ConfigMachines.Purifier.ticksPerPurify;
                fluidData = LiquidCrystalFluidTagData.fromStack(getInputTank().drain(ForgeDirection.UNKNOWN, ConfigMachines.Purifier.rclPerPurify, true));
                inventoryHelper.decrStackSize(PurifierContainer.SLOT_FILTERINPUT, 1);
                markDirty();
            }
        }
    }

    private static ForgeDirection[] directions = new ForgeDirection[] { ForgeDirection.UNKNOWN, ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.SOUTH };

    private void doPurify() {
        float purity = fluidData.getPurity();
        purity += ConfigMachines.Purifier.addedPurity / 100.0f;
        float maxPurity = ConfigMachines.Purifier.maxPurity / 100.0f;
        maxPurity *= fluidData.getQuality();
        if (purity > maxPurity) {
            purity = maxPurity;
        }
        fluidData.setPurity(purity);
        FluidStack stack = fluidData.makeLiquidCrystalStack();
        getOutputTank().fill(ForgeDirection.UNKNOWN, stack, true);
        fluidData = null;

        ItemStack spentMaterial = new ItemStack(ModItems.spentFilterMaterialItem, 1);
        inventoryLocator.ejectStack(worldObj, xCoord, yCoord, zCoord, spentMaterial, getCoordinate(), directions);
    }

    private boolean fillOutputTank() {
        return getOutputTank().fill(ForgeDirection.UNKNOWN, new FluidStack(DRFluidRegistry.liquidCrystal, ConfigMachines.Purifier.rclPerPurify), false) == ConfigMachines.Purifier.rclPerPurify;
    }

    private TileTank getInputTank() {
        if (topTank == null) {
            return bottomTank;
        }
        return topTank;
    }

    private TileTank getOutputTank() {
        if (bottomTank == null) {
            return topTank;
        }
        return bottomTank;
    }

    private boolean canWork() {
        if (bottomTank == null && topTank == null) {
            return false;
        }
        if (getInputTank().getFluidAmount() < ConfigMachines.Purifier.rclPerPurify) {
            return false;
        }
        if (getInputTank().getMultiBlock().equals(getOutputTank().getMultiBlock())) {
            // Same tank so operation is possible.
            return true;
        }
        if (!fillOutputTank()) {
            return false;
        }
        return true;
    }

    private boolean validSlot(){
        return inventoryHelper.getStackInSlot(PurifierContainer.SLOT_FILTERINPUT) != null && inventoryHelper.getStackInSlot(PurifierContainer.SLOT_FILTERINPUT).getItem() == ModItems.filterMaterialItem;
    }


    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("progress", progress);
        if (fluidData != null) {
            NBTTagCompound dataCompound = new NBTTagCompound();
            fluidData.writeDataToNBT(dataCompound);
            tagCompound.setTag("data", dataCompound);
            tagCompound.setInteger("amount", fluidData.getInternalTankAmount());
        }
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);

        writeBufferToNBT(tagCompound);
    }

    private void writeBufferToNBT(NBTTagCompound tagCompound) {
        NBTTagList bufferTagList = new NBTTagList();
        for (int i = 0 ; i < inventoryHelper.getCount() ; i++) {
            ItemStack stack = inventoryHelper.getStackInSlot(i);
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            if (stack != null) {
                stack.writeToNBT(nbtTagCompound);
            }
            bufferTagList.appendTag(nbtTagCompound);
        }
        tagCompound.setTag("Items", bufferTagList);
    }


    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        progress = tagCompound.getInteger("progress");
        if (tagCompound.hasKey("data")) {
            NBTTagCompound dataCompound = (NBTTagCompound) tagCompound.getTag("data");
            int amount = dataCompound.getInteger("amount");
            fluidData = LiquidCrystalFluidTagData.fromNBT(dataCompound, amount);
        } else {
            fluidData = null;
        }
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound);
    }

    private void readBufferFromNBT(NBTTagCompound tagCompound) {
        NBTTagList bufferTagList = tagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < bufferTagList.tagCount() ; i++) {
            NBTTagCompound nbtTagCompound = bufferTagList.getCompoundTagAt(i);
            inventoryHelper.setStackInSlot(i, ItemStack.loadItemStackFromNBT(nbtTagCompound));
        }
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

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { PurifierContainer.SLOT_FILTERINPUT };
    }

    @Override
    public boolean canInsertItem(int index, ItemStack item, int side) {
        return PurifierContainer.factory.isInputSlot(index) || PurifierContainer.factory.isSpecificItemSlot(index);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack item, int side) {
        return PurifierContainer.factory.isOutputSlot(index);
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
        return "Purifier Inventory";
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
        return canPlayerAccess(player);
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
