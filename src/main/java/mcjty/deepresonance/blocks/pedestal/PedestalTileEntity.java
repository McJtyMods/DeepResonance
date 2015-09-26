package mcjty.deepresonance.blocks.pedestal;

import mcjty.container.InventoryHelper;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.varia.InventoryLocator;
import mcjty.entity.GenericTileEntity;
import mcjty.varia.BlockTools;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class PedestalTileEntity extends GenericTileEntity implements IInventory {
    private InventoryHelper inventoryHelper = new InventoryHelper(this, PedestalContainer.factory, 1);

    private int checkCounter = 0;

    // Cache for the inventory used to put the spent crystal material in.
    private InventoryLocator inventoryLocator = new InventoryLocator();


    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    protected void checkStateServer() {
        checkCounter--;
        if (checkCounter > 0) {
            return;
        }
        checkCounter = 20;

        ForgeDirection orientation = BlockTools.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        int xx = xCoord + orientation.offsetX;
        int yy = yCoord + orientation.offsetY;
        int zz = zCoord + orientation.offsetZ;
        Block block = worldObj.getBlock(xx, yy, zz);
        if (block.isAir(worldObj, xx, yy, zz)) {
            // Nothing in front. We can place a new crystal if we have one.
            placeCrystal(xx, yy, zz);
        } else if (block == ModBlocks.resonatingCrystalBlock) {
            // Check if the crystal in front of us still has power.
            // If not we will remove it.
            checkCrystal(xx, yy, zz);
        } // else we can do nothing.
    }

    private void placeCrystal(int xx, int yy, int zz) {
        ItemStack crystalStack = inventoryHelper.getStackInSlot(PedestalContainer.SLOT_CRYSTAL);
        if (crystalStack != null && crystalStack.stackSize > 0) {
            if (crystalStack.getItem() instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) (crystalStack.getItem());
                itemBlock.placeBlockAt(crystalStack, null, worldObj, xx, yy, zz, 0, 0, 0, 0, 0);
                inventoryHelper.decrStackSize(PedestalContainer.SLOT_CRYSTAL, 1);
            }
        }
    }

    private static ForgeDirection[] directions = new ForgeDirection[] { ForgeDirection.UNKNOWN,
            ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.SOUTH,
            ForgeDirection.UP, ForgeDirection.DOWN
    };

    private void checkCrystal(int xx, int yy, int zz) {
        TileEntity tileEntity = worldObj.getTileEntity(xx, yy, zz);
        if (tileEntity instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) tileEntity;
            if (resonatingCrystalTileEntity.getPower() <= EnergyCollectorTileEntity.CRYSTAL_MIN_POWER) {
                ItemStack spentCrystal = new ItemStack(ModBlocks.resonatingCrystalBlock, 1);
                NBTTagCompound tagCompound = new NBTTagCompound();
                resonatingCrystalTileEntity.writeToNBT(tagCompound);
                spentCrystal.setTagCompound(tagCompound);
                inventoryLocator.ejectStack(worldObj, xCoord, yCoord, zCoord, spentCrystal, getCoordinate(), directions);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
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
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
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
        return "Pedestal Inventory";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
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
        return stack.getItem() == Item.getItemFromBlock(ModBlocks.resonatingCrystalBlock);
    }


}
