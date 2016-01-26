package mcjty.deepresonance.blocks.crystalizer;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.base.ElecEnergyReceiverTileBase;
import mcjty.deepresonance.blocks.tank.ITankHook;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.network.Argument;
import mcjty.lib.network.PacketRequestIntegerFromServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class CrystalizerTileEntity extends ElecEnergyReceiverTileBase implements ITankHook, DefaultSidedInventory, ITickable {

    public static final String CMD_GETPROGRESS = "getProgress";
    public static final String CLIENTCMD_GETPROGRESS = "getProgress";

    private InventoryHelper inventoryHelper = new InventoryHelper(this, CrystalizerContainer.factory, 1);

    public CrystalizerTileEntity() {
        super(ConfigMachines.Crystalizer.rfMaximum, ConfigMachines.Crystalizer.rfPerTick);
    }

    private TileTank rclTank;
    private static int totalProgress = 0;
    private int progress = 0;
    private LiquidCrystalFluidTagData mergedData = null;

    private static int clientProgress = 0;

    public static int getTotalProgress() {
        if (totalProgress == 0) {
            totalProgress = ConfigMachines.Crystalizer.rclPerCrystal / ConfigMachines.Crystalizer.rclPerTick;
        }
        return totalProgress;
    }

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
        if (!canCrystalize()) {
            return;
        }

        storage.extractEnergy(ConfigMachines.Crystalizer.rfPerRcl, false);
        FluidStack fluidStack = rclTank.drain(null, ConfigMachines.Crystalizer.rclPerTick, true);
        LiquidCrystalFluidTagData data = LiquidCrystalFluidTagData.fromStack(fluidStack);
        if (mergedData == null) {
            mergedData = data;
        } else {
            mergedData.merge(data);
        }

        handleProgress();
    }

    private void handleProgress() {
        progress++;
        if (progress == 1) {
            // We just started to work. Notify client
            worldObj.markBlockForUpdate(getPos());
        }

        if (progress >= getTotalProgress()) {
            progress = 0;
            makeCrystal();
            markDirtyClient();
        }

        markDirty();
    }

    public int getProgress() {
        return progress;
    }

    public static int getClientProgress() {
        return clientProgress;
    }

    private boolean canCrystalize() {
        if (rclTank == null) {
            return false;
        }

        if (storage.getEnergyStored() < ConfigMachines.Crystalizer.rfPerRcl) {
            return false;
        }

        if (hasCrystal()) {
            return false;
        }

        FluidStack fluidStack = rclTank.drain(null, ConfigMachines.Crystalizer.rclPerTick, false);
        if (fluidStack == null || fluidStack.amount != ConfigMachines.Crystalizer.rclPerTick) {
            return false;
        }
        return true;
    }

    public boolean hasCrystal() {
        ItemStack crystalStack = inventoryHelper.getStackInSlot(CrystalizerContainer.SLOT_CRYSTAL);
        if (crystalStack != null) {
            return true;
        }
        return false;
    }

    private void makeCrystal() {
        ItemStack stack = new ItemStack(ModBlocks.resonatingCrystalBlock);
        NBTTagCompound compound = new NBTTagCompound();
        compound.setFloat("power", 100.0f);
        compound.setFloat("strength", mergedData.getStrength() * 100.0f);
        compound.setFloat("efficiency", mergedData.getEfficiency() * 100.0f);
        compound.setFloat("purity", mergedData.getPurity() * 100.0f);
        compound.setByte("version", (byte) 2);      // Legacy support to support older crystals.
        stack.setTagCompound(compound);
        mergedData = null;
        inventoryHelper.setStackInSlot(CrystalizerContainer.SLOT_CRYSTAL, stack);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("progress", progress);
        if (mergedData != null) {
            NBTTagCompound dataCompound = new NBTTagCompound();
            mergedData.writeDataToNBT(dataCompound);
            tagCompound.setTag("data", dataCompound);
            tagCompound.setInteger("amount", mergedData.getInternalTankAmount());
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
            mergedData = LiquidCrystalFluidTagData.fromNBT(dataCompound, amount);
        } else {
            mergedData = null;
        }
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
    }

    @Override
    public void hook(TileTank tank, EnumFacing direction) {
        if (direction != EnumFacing.DOWN) {
            return;
        } else if (rclTank == null){
            if (validRCLTank(tank)){
                rclTank = tank;
            }
        }
    }

    @Override
    public void unHook(TileTank tank, EnumFacing direction) {
        if (tilesEqual(rclTank, tank)){
            rclTank = null;
            notifyNeighboursOfDataChange();
        }
    }

    @Override
    public void onContentChanged(TileTank tank, EnumFacing direction) {
        if (tilesEqual(rclTank, tank)){
            if (!validRCLTank(tank)) {
                rclTank = null;
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
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] { CrystalizerContainer.SLOT_CRYSTAL };
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == CrystalizerContainer.SLOT_CRYSTAL;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() == Item.getItemFromBlock(ModBlocks.resonatingCrystalBlock);
    }


    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0;
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
            return progress * 100 / getTotalProgress();
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
            clientProgress = result;
            return true;
        }
        return false;
    }
}
