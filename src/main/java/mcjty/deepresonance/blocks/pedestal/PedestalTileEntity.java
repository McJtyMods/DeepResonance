package mcjty.deepresonance.blocks.pedestal;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.collector.EnergyCollectorSetup;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.varia.InventoryLocator;
import mcjty.deepresonance.varia.Tools;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraft.util.EnumFacing;

public class PedestalTileEntity extends GenericTileEntity implements IInventory, ITickable {
    private InventoryHelper inventoryHelper = new InventoryHelper(this, PedestalContainer.factory, 1);

    private int checkCounter = 0;

    // Cache for the inventory used to put the spent crystal material in.
    private InventoryLocator inventoryLocator = new InventoryLocator();

    private BlockPos cachedLocator = null;

    @Override
    public void update() {
        if (!worldObj.isRemote){
            checkStateServer();
        }
    }

    protected void checkStateServer() {
        checkCounter--;
        if (checkCounter > 0) {
            return;
        }
        checkCounter = 20;

        EnumFacing orientation = EnumFacing.NORTH;//BlockTools.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        BlockPos b = pos.offset(orientation);
        if (worldObj.isAirBlock(b)) {
            // Nothing in front. We can place a new crystal if we have one.
            placeCrystal(b);
        } else if (WorldHelper.getBlockAt(worldObj, b) == ModBlocks.resonatingCrystalBlock) {
            // Check if the crystal in front of us still has power.
            // If not we will remove it.
            checkCrystal(b);
        } // else we can do nothing.
    }

    private void placeCrystal(BlockPos pos) {
        ItemStack crystalStack = inventoryHelper.getStackInSlot(PedestalContainer.SLOT_CRYSTAL);
        if (crystalStack != null && crystalStack.stackSize > 0) {
            if (crystalStack.getItem() instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) (crystalStack.getItem());
                itemBlock.placeBlockAt(crystalStack, FakePlayerFactory.getMinecraft((WorldServer) worldObj), worldObj, pos, null, 0, 0, 0, itemBlock.getBlock().getStateFromMeta(0));
                inventoryHelper.decrStackSize(PedestalContainer.SLOT_CRYSTAL, 1);
                Tools.playSound(worldObj, ModBlocks.resonatingCrystalBlock.stepSound.getBreakSound(), getPos().getX(), getPos().getY(), getPos().getZ(), 1.0f, 1.0f);

                if (findCollector(pos)) {
                    TileEntity tileEntity = WorldHelper.getTileAt(worldObj, new BlockPos(cachedLocator));
                    if (tileEntity instanceof EnergyCollectorTileEntity) {
                        EnergyCollectorTileEntity energyCollectorTileEntity = (EnergyCollectorTileEntity) tileEntity;
                        energyCollectorTileEntity.addCrystal(pos.getX(), pos.getY(), pos.getZ());
                    }
                }
            }
        }
    }

    private static EnumFacing[] directions = new EnumFacing[] {
            EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH,
            EnumFacing.UP, EnumFacing.DOWN
    };

    private void checkCrystal(BlockPos p) {
        TileEntity tileEntity = WorldHelper.getTileAt(worldObj, p);
        if (tileEntity instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) tileEntity;
            if (resonatingCrystalTileEntity.getPower() <= EnergyCollectorTileEntity.CRYSTAL_MIN_POWER) {
                ItemStack spentCrystal = new ItemStack(ModBlocks.resonatingCrystalBlock, 1);
                NBTTagCompound tagCompound = new NBTTagCompound();
                resonatingCrystalTileEntity.writeToNBT(tagCompound);
                spentCrystal.setTagCompound(tagCompound);
                inventoryLocator.ejectStack(worldObj, getPos().getX(), getPos().getY(), getPos().getZ(), spentCrystal, pos, directions);
                worldObj.setBlockToAir(p);
                Tools.playSound(worldObj, ModBlocks.resonatingCrystalBlock.stepSound.getBreakSound(), p.getX(), p.getY(), p.getZ(), 1.0f, 1.0f);
            }
        }
    }

    private boolean findCollector(BlockPos crystalLocation) {
        if (cachedLocator != null) {
            if (WorldHelper.getBlockAt(worldObj, crystalLocation) == EnergyCollectorSetup.energyCollectorBlock) {
                return true;
            }
            cachedLocator = null;
        }

        float closestDistance = Float.MAX_VALUE;

        int yy = crystalLocation.getY(), xx = crystalLocation.getX(), zz = crystalLocation.getZ();
        for (int y = yy - ConfigMachines.Collector.maxVerticalCrystalDistance ; y <= yy + ConfigMachines.Collector.maxVerticalCrystalDistance ; y++) {
            if (y >= 0 && y < worldObj.getHeight()) {
                int maxhordist = ConfigMachines.Collector.maxHorizontalCrystalDistance;
                for (int x = xx - maxhordist; x <= xx + maxhordist; x++) {
                    for (int z = zz - maxhordist; z <= zz + maxhordist; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (WorldHelper.getBlockAt(worldObj, pos) == EnergyCollectorSetup.energyCollectorBlock) {
                            double sqdist = pos.distanceSq(crystalLocation);
                            if (sqdist < closestDistance) {
                                closestDistance = (float)sqdist;
                                cachedLocator = pos;
                            }
                        }
                    }
                }
            }
        }
        return cachedLocator != null;
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
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventoryHelper.setInventorySlotContents(getInventoryStackLimit(), index, stack);
    }

    @Override
    public String getName() {
        return "Pedestal Inventory";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    @Override
    public IChatComponent getDisplayName() {
        return null;
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
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() == Item.getItemFromBlock(ModBlocks.resonatingCrystalBlock);
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }


}
