package mcjty.deepresonance.blocks.laser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.lens.LensSetup;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericEnergyReceiverTileEntity;
import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.Coordinate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class LaserTileEntity extends GenericEnergyReceiverTileEntity implements ISidedInventory {

    private int counter = 10;
    private int color = 0;      // 0 means not active, > 0 means a color laser

    private InventoryHelper inventoryHelper = new InventoryHelper(this, LaserContainer.factory, 1);

    public LaserTileEntity() {
        super(ConfigMachines.Laser.rfMaximum, ConfigMachines.Laser.rfPerTick);
    }

    @Override
    protected void checkStateServer() {
        counter--;
        if (counter > 0) {
            return;
        }
        counter = 10;

        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        if (findLens(meta)) {
            changeColor(1);
        } else {
            changeColor(0);
        }
    }

    private void changeColor(int newcolor) {
        if (newcolor != color) {
            color = newcolor;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            markDirty();
        }
    }

    public int getColor() {
        return color;
    }

    private boolean findLens(int meta) {
        ForgeDirection direction = BlockTools.getOrientationHoriz(meta);
        Coordinate shouldBeAir = getCoordinate().addDirection(direction);
        if (!worldObj.isAirBlock(shouldBeAir.getX(), shouldBeAir.getY(), shouldBeAir.getZ())) {
            return false;
        }
        Coordinate shouldBeLens = shouldBeAir.addDirection(direction);
        Block lensBlock = worldObj.getBlock(shouldBeLens.getX(), shouldBeLens.getY(),shouldBeLens.getZ());
        if (lensBlock != LensSetup.lensBlock) {
            return false;
        }
        ForgeDirection lensDirection = BlockTools.getOrientationHoriz(worldObj.getBlockMetadata(shouldBeLens.getX(), shouldBeLens.getY(), shouldBeLens.getZ()));
        if (lensDirection != direction) {
            return false;
        }

        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        color = tagCompound.getInteger("color");
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
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
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("color", color);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
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
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        // @todo needs a better box
        return AxisAlignedBB.getBoundingBox(xCoord - 7, yCoord - 1, zCoord - 7, xCoord + 8, yCoord + 2, zCoord + 8);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack item, int side) {
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { LaserContainer.SLOT_CATALYST, LaserContainer.SLOT_CRYSTAL };
    }

    @Override
    public boolean canInsertItem(int index, ItemStack item, int side) {
        switch (index) {
            case LaserContainer.SLOT_CRYSTAL:
                return item.isItemEqual(new ItemStack(ModBlocks.resonatingCrystalBlock));
            case LaserContainer.SLOT_CATALYST:
                return true;
        }
        return false;
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
        inventoryHelper.setInventorySlotContents(index == LaserContainer.SLOT_CRYSTAL ? 1 : 64, index, stack);
    }

    @Override
    public String getInventoryName() {
        return "Laser Inventory";
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
        if (index == LaserContainer.SLOT_CRYSTAL) {
            return stack.getItem() == Item.getItemFromBlock(ModBlocks.resonatingCrystalBlock);
        } else {
            return true;
        }
    }
}
