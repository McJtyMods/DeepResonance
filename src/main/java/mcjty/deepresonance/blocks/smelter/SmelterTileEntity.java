package mcjty.deepresonance.blocks.smelter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.base.ElecEnergyReceiverTileBase;
import mcjty.deepresonance.blocks.tank.ITankHook;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.network.Argument;
import mcjty.lib.network.PacketRequestIntegerFromServer;
import mcjty.lib.varia.BlockTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

/**
 * Created by Elec332 on 9-8-2015.
 */
public class SmelterTileEntity extends ElecEnergyReceiverTileBase implements ITankHook, ISidedInventory {

    public static final String CMD_GETPROGRESS = "getProgress";
    public static final String CLIENTCMD_GETPROGRESS = "getProgress";

    private InventoryHelper inventoryHelper = new InventoryHelper(this, SmelterContainer.factory, 1);

    public SmelterTileEntity() {
        super(ConfigMachines.Smelter.rfMaximum, ConfigMachines.Smelter.rfPerTick);
        checkTanks = true;
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
            int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
            int meta2;
            if (canWork() && validSlot()) {
                startSmelting();
                meta2 = BlockTools.setRedstoneSignalIn(meta, true);
            } else {
                meta2 = BlockTools.setRedstoneSignalIn(meta, false);
            }
            if (meta != meta2) {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta2, 3);
            }
        }
    }

    @SideOnly(Side.CLIENT)
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
                && rclTank.fill(ForgeDirection.UNKNOWN, new FluidStack(DRFluidRegistry.liquidCrystal, ConfigMachines.Smelter.rclPerOre), false) == ConfigMachines.Smelter.rclPerOre;
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

        lavaTank.drain(ForgeDirection.UNKNOWN, new FluidStack(FluidRegistry.LAVA, ConfigMachines.Smelter.lavaCost), true);

        progress = ConfigMachines.Smelter.processTime + (int) ((percentage - 0.5f) * ConfigMachines.Smelter.processTime);
        totalProgress = progress;
    }

    private void stopSmelting() {
        if (finalQuality > 0.0f) {
            FluidStack stack = LiquidCrystalFluidTagData.makeLiquidCrystalStack(ConfigMachines.Smelter.rclPerOre, finalQuality, finalPurity, 0.1f, 0.1f);
            rclTank.fill(ForgeDirection.UNKNOWN, stack, true);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("totalProgress", totalProgress);
        tagCompound.setInteger("progress", progress);
        tagCompound.setFloat("finalQuality", finalQuality);
        tagCompound.setFloat("finalPurity", finalPurity);
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
        totalProgress = tagCompound.getInteger("totalProgress");
        progress = tagCompound.getInteger("progress");
        finalQuality = tagCompound.getFloat("finalQuality");
        finalPurity = tagCompound.getFloat("finalPurity");
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
            this.lavaTank = tank;
        } else if (rclTank == null){
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
        return first != null && second != null && first.myLocation().equals(second.myLocation()) && WorldHelper.getDimID(first.getWorldObj()) == WorldHelper.getDimID(second.getWorldObj());
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { SmelterContainer.SLOT_OREINPUT };
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
        return "Smelter Inventory";
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

    // Request the researching amount from the server. This has to be called on the client side.
    public void requestProgressFromServer() {
        DeepResonance.networkHandler.getNetworkWrapper().sendToServer(new PacketRequestIntegerFromServer(xCoord, yCoord, zCoord,
                CMD_GETPROGRESS,
                CLIENTCMD_GETPROGRESS));
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


}
