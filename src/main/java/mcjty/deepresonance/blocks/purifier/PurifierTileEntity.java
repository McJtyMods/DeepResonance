package mcjty.deepresonance.blocks.purifier;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.tank.ITankHook;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.deepresonance.items.ModItems;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.container.InventoryLocator;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.CustomSidedInvWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Random;

public class PurifierTileEntity extends GenericTileEntity implements ITankHook, DefaultSidedInventory, ITickable {

    private InventoryHelper inventoryHelper = new InventoryHelper(this, PurifierContainer.factory, 1);

    public PurifierTileEntity() {
    }

    private TileTank bottomTank;
    private TileTank topTank;
    private int progress = 0;

    // Cache for the inventory used to put the spent filter material in.
    private InventoryLocator inventoryLocator = new InventoryLocator();

    private static Random random = new Random();

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    public void update() {
        if (!worldObj.isRemote){
            checkStateServer();
        }
    }

    private void checkStateServer() {
        if (progress > 0) {
            progress--;
            if (progress == 0) {
                // Done. First check if we can actually insert the liquid. If not we postpone this.
                progress = 1;
                if (getOutputTank() != null) {
                    if (canWork()) {
                        LiquidCrystalFluidTagData fluidData = LiquidCrystalFluidTagData.fromStack(getInputTank().drain(null, ConfigMachines.Purifier.rclPerPurify, true));
                        if (fluidData != null) {
                            if (random.nextInt(doPurify(fluidData)) == 0) {
                                consumeFilter();
                            }
                        }
                        progress = 0;   // Really done
                    }
                }
            }
            markDirty();
        } else {
            if (canWork()) {
                progress = ConfigMachines.Purifier.ticksPerPurify;
                markDirty();
            }
        }
    }

    private static EnumFacing[] directions = new EnumFacing[] { null, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH };

    private void consumeFilter() {
        inventoryHelper.decrStackSize(PurifierContainer.SLOT_FILTERINPUT, 1);
        ItemStack spentMaterial = new ItemStack(ModItems.spentFilterMaterialItem, 1);
        inventoryLocator.ejectStack(worldObj, pos, spentMaterial, pos, directions);
    }

    private int doPurify(LiquidCrystalFluidTagData fluidData) {
        float purity = fluidData.getPurity();
        float maxPurityToAdd = ConfigMachines.Purifier.addedPurity / 100.0f;
        float addedPurity = maxPurityToAdd;
        float maxPurity = (ConfigMachines.Purifier.maxPurity + .1f) / 100.0f;
        maxPurity *= fluidData.getQuality();
        if (purity + addedPurity > maxPurity) {
            addedPurity = maxPurity - purity;
            if (addedPurity < 0.0001f) {
                // We are already very pure. Do nothing.
                // Put back the fluid we extracted.
                FluidStack stack = fluidData.makeLiquidCrystalStack();
                getOutputTank().fill(null, stack, true);
                return 1000000;
            }
        }

        purity += addedPurity;
        fluidData.setPurity(purity);
        FluidStack stack = fluidData.makeLiquidCrystalStack();
        getOutputTank().fill(null, stack, true);
        return (int) ((maxPurityToAdd - addedPurity) * 40 / maxPurityToAdd + 1);
    }

    private boolean testFillOutputTank() {
        return getOutputTank().fill(null, new FluidStack(DRFluidRegistry.liquidCrystal, ConfigMachines.Purifier.rclPerPurify), false) == ConfigMachines.Purifier.rclPerPurify;
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
        if (!validSlot()) {
            return false;
        }
        // Same tank so operation is possible.
        return getInputTank().getMultiBlock().equals(getOutputTank().getMultiBlock()) || testFillOutputTank();
    }

    private boolean validSlot(){
        return inventoryHelper.getStackInSlot(PurifierContainer.SLOT_FILTERINPUT) != null && inventoryHelper.getStackInSlot(PurifierContainer.SLOT_FILTERINPUT).getItem() == ModItems.filterMaterialItem;
    }


    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("progress", progress);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        writeBufferToNBT(tagCompound, inventoryHelper);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        progress = tagCompound.getInteger("progress");
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
    }

    @Override
    public void hook(TileTank tank, EnumFacing direction) {
        if (direction == EnumFacing.DOWN){
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
    public void unHook(TileTank tank, EnumFacing direction) {
        if (tilesEqual(bottomTank, tank)){
            bottomTank = null;
            notifyAndMarkDirty();
        } else if (tilesEqual(topTank, tank)){
            topTank = null;
            notifyAndMarkDirty();
        }
    }



    @Override
    public void onContentChanged(TileTank tank, EnumFacing direction) {
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
        return first != null && second != null && first.getPos().equals(second.getPos()) && WorldHelper.getDimID(first.getWorld()) == WorldHelper.getDimID(second.getWorld());
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] { PurifierContainer.SLOT_FILTERINPUT };
    }

    @Override
    public boolean canInsertItem(int index, ItemStack item, EnumFacing side) {
        if (!isItemValidForSlot(index, item)) {
            return false;
        }
        return PurifierContainer.factory.isInputSlot(index) || PurifierContainer.factory.isSpecificItemSlot(index);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack item, EnumFacing side) {
        return PurifierContainer.factory.isOutputSlot(index);
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
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() == ModItems.filterMaterialItem;
    }

    private IItemHandler invHandler = new CustomSidedInvWrapper(this);

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) invHandler;
        }
        return super.getCapability(capability, facing);
    }

    protected void notifyAndMarkDirty(){
        if (WorldHelper.chunkLoaded(worldObj, pos)){
            this.markDirty();
            this.worldObj.notifyNeighborsOfStateChange(pos, blockType);
        }
    }

}
