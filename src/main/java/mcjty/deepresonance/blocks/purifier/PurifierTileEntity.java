package mcjty.deepresonance.blocks.purifier;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.tank.ITankHook;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.deepresonance.items.ModItems;
import mcjty.deepresonance.varia.InventoryLocator;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericTileEntity;
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
import net.minecraftforge.items.wrapper.InvWrapper;

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

    private LiquidCrystalFluidTagData fluidData = null;

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

    protected void checkStateServer() {
        if (progress > 0) {
            progress--;
            if (progress == 0) {
                if (fluidData != null) {
                    // Done. First check if we can actually insert the liquid. If not we postpone this.
                    progress = 1;
                    if (getOutputTank() != null) {
                        if (testFillOutputTank() && validSlot()) {
                            if (random.nextInt(doPurify()) == 0) {
                                consumeFilter();
                            }
                            progress = 0;   // Really done
                        }
                    }
                }
            }
            markDirty();
        } else {
            if (canWork() && validSlot()) {
                progress = ConfigMachines.Purifier.ticksPerPurify;
                fluidData = LiquidCrystalFluidTagData.fromStack(getInputTank().drain(null, ConfigMachines.Purifier.rclPerPurify, true));
                markDirty();
            }
        }
    }

    private static EnumFacing[] directions = new EnumFacing[] { null, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH };

    private void consumeFilter() {
        inventoryHelper.decrStackSize(PurifierContainer.SLOT_FILTERINPUT, 1);
        ItemStack spentMaterial = new ItemStack(ModItems.spentFilterMaterialItem, 1);
        inventoryLocator.ejectStack(worldObj, pos.getX(), pos.getY(), pos.getZ(), spentMaterial, pos, directions);
    }

    private int doPurify() {
        float purity = fluidData.getPurity();
        float maxPurityToAdd = ConfigMachines.Purifier.addedPurity / 100.0f;
        float addedPurity = maxPurityToAdd;
        float maxPurity = ConfigMachines.Purifier.maxPurity / 100.0f;
        maxPurity *= fluidData.getQuality();
        if (purity + addedPurity > maxPurity) {
            addedPurity = maxPurity - purity;
            if (addedPurity < 0.0001f) {
                // We are already very pure. Do nothing.
                // Put back the fluid we extracted.
                FluidStack stack = fluidData.makeLiquidCrystalStack();
                getOutputTank().fill(null, stack, true);
                fluidData = null;
                return 1000;
            }
        }

        purity += addedPurity;
        fluidData.setPurity(purity);
        FluidStack stack = fluidData.makeLiquidCrystalStack();
        getOutputTank().fill(null, stack, true);
        fluidData = null;
        return (int) ((maxPurityToAdd - addedPurity) * 1000 / maxPurityToAdd + 1);
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
        writeBufferToNBT(tagCompound, inventoryHelper);
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
            notifyAndMB();
        } else if (tilesEqual(topTank, tank)){
            topTank = null;
            notifyAndMB();
        }
    }

    private void notifyAndMB(){
        if (WorldHelper.chunkLoaded(worldObj, pos)){
            this.markDirty();
            this.worldObj.notifyNeighborsOfStateChange(pos, blockType);
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

    private IItemHandler invHandler = new InvWrapper(this);

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
}
