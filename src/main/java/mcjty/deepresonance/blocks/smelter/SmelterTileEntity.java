package mcjty.deepresonance.blocks.smelter;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.tank.ITankHook;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericEnergyReceiverTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.network.PacketRequestIntegerFromServer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

/**
 * Created by Elec332 on 9-8-2015.
 */
public class SmelterTileEntity extends GenericEnergyReceiverTileEntity implements ITankHook, DefaultSidedInventory, ITickable {

    public static final String CMD_GETPROGRESS = "getProgress";
    public static final String CLIENTCMD_GETPROGRESS = "getProgress";

    private InventoryHelper inventoryHelper = new InventoryHelper(this, SmelterContainer.factory, 1);

    public SmelterTileEntity() {
        super(ConfigMachines.Smelter.rfMaximum, ConfigMachines.Smelter.rfPerTick);
        checkTanks = true;
    }

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    private int totalProgress = 0;
    private int progress = 0;
    private TileTank lavaTank;
    private TileTank rclTank;
    private boolean checkTanks;
    private float finalQuality = 1.0f;  // Calculated quality based on the amount of lava in the lava tank
    private float finalPurity = 0.1f;   // Calculated quality based on the amount of lava in the lava tank

    private static int progressPercentage = 0;

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
            if (canWork()) {
                progress--;
                storage.extractEnergy(ConfigMachines.Smelter.rfPerOre, false);
                if (progress == 0) {
                    // Done!
                    stopSmelting();
                }
            }
        } else {
            IBlockState state = worldObj.getBlockState(getPos());
            boolean oldworking = state.getValue(SmelterBlock.WORKING);
            boolean newworking;
            if (canWork() && validSlot()) {
                startSmelting();
                newworking = true;
            } else {
                newworking = false;
            }
            if (newworking != oldworking) {
                state = state.withProperty(SmelterBlock.WORKING, newworking);
                worldObj.setBlockState(getPos(), state, 3);
            }
        }
    }

    public static int getProgressPercentage() {
        return progressPercentage;
    }

    private boolean canWork() {
        if (checkTanks) {
            if (checkTanks()) {
                checkTanks = false;
            } else {
                return false;
            }
        }
        return storage.getEnergyStored() >= ConfigMachines.Smelter.rfPerOre;
    }

    private boolean checkTanks(){
        return lavaTank != null && rclTank != null
                && DRFluidRegistry.getFluidFromStack(lavaTank.getFluid()) == FluidRegistry.LAVA
                && lavaTank.getFluidAmount() > lavaTank.getCapacity()*0.25f
                && rclTank.fill(null, new FluidStack(DRFluidRegistry.liquidCrystal, ConfigMachines.Smelter.rclPerOre), false) == ConfigMachines.Smelter.rclPerOre;
    }

    private boolean validSlot(){
        return inventoryHelper.getStackInSlot(SmelterContainer.SLOT_OREINPUT) != null && inventoryHelper.getStackInSlot(SmelterContainer.SLOT_OREINPUT).getItem() == Item.getItemFromBlock(ModBlocks.resonatingOreBlock);
    }


    private void startSmelting() {
        inventoryHelper.decrStackSize(SmelterContainer.SLOT_OREINPUT, 1);

        float percentage = (float)lavaTank.getFluidAmount() / lavaTank.getCapacity();

        if (percentage < 0.40f) {
            // Slower smelting progress and slightly reduced quality
            finalQuality = 1.0f - (0.40f - percentage);
            finalPurity = 0.1f;
        } else if (percentage > 0.75f) {
            finalQuality = -1.0f;   // Total waste!
            finalPurity = 0.0f;
        } else if (percentage > 0.60f) {
            // Reduced quality.
            finalQuality = 1.0f - (percentage - 0.60f) * 6.666f;
            finalPurity = 0.1f - (percentage - 0.60f) * 0.3f;
        } else {
            finalQuality = 1.0f;
            finalPurity = 0.1f;
        }

        lavaTank.drain(null, new FluidStack(FluidRegistry.LAVA, ConfigMachines.Smelter.lavaCost), true);

        progress = ConfigMachines.Smelter.processTime + (int) ((percentage - 0.5f) * ConfigMachines.Smelter.processTime);
        totalProgress = progress;
    }

    private void stopSmelting() {
        if (finalQuality > 0.0f) {
            FluidStack stack = LiquidCrystalFluidTagData.makeLiquidCrystalStack(ConfigMachines.Smelter.rclPerOre, finalQuality, finalPurity, 0.1f, 0.1f);
            rclTank.fill(null, stack, true);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("totalProgress", totalProgress);
        tagCompound.setInteger("progress", progress);
        tagCompound.setFloat("finalQuality", finalQuality);
        tagCompound.setFloat("finalPurity", finalPurity);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        writeBufferToNBT(tagCompound, inventoryHelper);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        totalProgress = tagCompound.getInteger("totalProgress");
        progress = tagCompound.getInteger("progress");
        finalQuality = tagCompound.getFloat("finalQuality");
        finalPurity = tagCompound.getFloat("finalPurity");
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
    }

    @Override
    public void hook(TileTank tank, EnumFacing direction) {
        if (direction == EnumFacing.DOWN){
            this.lavaTank = tank;
        } else if (rclTank == null){
            if (validRCLTank(tank)){
                rclTank = tank;
            }
        }
        checkTanks = true;
    }

    @Override
    public void unHook(TileTank tank, EnumFacing direction) {
        if (tilesEqual(lavaTank, tank)){
            lavaTank = null;
        } else if (tilesEqual(rclTank, tank)){
            rclTank = null;
            notifyAndMarkDirty();
        }
        checkTanks = true;
    }

    @Override
    public void onContentChanged(TileTank tank, EnumFacing direction) {
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
        return first != null && second != null && first.getPos().equals(second.getPos()) && WorldHelper.getDimID(first.getWorld()) == WorldHelper.getDimID(second.getWorld());
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] { SmelterContainer.SLOT_OREINPUT };
    }

    @Override
    public boolean canInsertItem(int index, ItemStack item, EnumFacing side) {
        if (!isItemValidForSlot(index, item)) {
            return false;
        }
        return SmelterContainer.factory.isInputSlot(index) || SmelterContainer.factory.isSpecificItemSlot(index);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack item, EnumFacing side) {
        return SmelterContainer.factory.isOutputSlot(index);
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
        return stack.getItem() == Item.getItemFromBlock(ModBlocks.resonatingOreBlock);
    }

    // Request the researching amount from the server. This has to be called on the client side.
    public void requestProgressFromServer() {
        DeepResonance.networkHandler.getNetworkWrapper().sendToServer(new PacketRequestIntegerFromServer(DeepResonance.MODID, pos, CMD_GETPROGRESS, CLIENTCMD_GETPROGRESS));
    }

    @Override
    public Integer executeWithResultInteger(String command, Map<String, Argument> args) {
        Integer rc = super.executeWithResultInteger(command, args);
        if (rc != null) {
            return rc;
        }
        if (CMD_GETPROGRESS.equals(command)) {
            if (totalProgress == 0) {
                return 0;
            } else {
                return (totalProgress - progress) * 100 / totalProgress;
            }
        }
        return null;
    }

    @Override
    public boolean execute(String command, Integer result) {
        boolean rc = super.execute(command, result);
        if (rc) {
            return true;
        }
        if (CLIENTCMD_GETPROGRESS.equals(command)) {
            progressPercentage = result;
            return true;
        }
        return false;
    }

    protected void notifyAndMarkDirty(){
        if (WorldHelper.chunkLoaded(worldObj, pos)){
            this.markDirty();
            this.worldObj.notifyNeighborsOfStateChange(pos, blockType);
        }
    }

}
