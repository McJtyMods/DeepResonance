package mcjty.deepresonance.blocks.pedestal;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.collector.EnergyCollectorSetup;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.container.InventoryLocator;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.tools.ItemStackTools;
import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.SoundTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.Optional;

public class PedestalTileEntity extends GenericTileEntity implements DefaultSidedInventory, ITickable {
    private InventoryHelper inventoryHelper = new InventoryHelper(this, PedestalContainer.factory, 1);

    private int checkCounter = 0;

    // Cache for the inventory used to put the spent crystal material in.
    private InventoryLocator inventoryLocator = new InventoryLocator();

    private BlockPos cachedLocator = null;

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    @Override
    public void update() {
        if (!getWorld().isRemote){
            checkStateServer();
        }
    }

    protected void checkStateServer() {
        checkCounter--;
        if (checkCounter > 0) {
            return;
        }
        checkCounter = 20;

        BlockPos b = this.getCrystalPosition();
        if (getWorld().isAirBlock(b)) {
            // Nothing in front. We can place a new crystal if we have one.
            placeCrystal();
        } else if (WorldHelper.getBlockAt(getWorld(), b) == ModBlocks.resonatingCrystalBlock) {
            // Check if the crystal in front of us still has power.
            // If not we will remove it.
            checkCrystal();
        } // else we can do nothing.
    }

    public BlockPos getCrystalPosition() {
        IBlockState state = getWorld().getBlockState(getPos());
        EnumFacing orientation = BlockTools.getOrientation(state.getBlock().getMetaFromState(state));
        return pos.offset(orientation);
    }

    public Optional<ResonatingCrystalTileEntity> getCrystal() {
        BlockPos p = this.getCrystalPosition();
        TileEntity t = WorldHelper.getTileAt(getWorld(), p);
        if (t instanceof ResonatingCrystalTileEntity) {
            return Optional.of((ResonatingCrystalTileEntity)t);
        }
        return Optional.empty();
    }

    public boolean crystalPresent() {
        return WorldHelper.getBlockAt(getWorld(), this.getCrystalPosition()) == ModBlocks.resonatingCrystalBlock;
    }

    private void placeCrystal() {
        BlockPos pos = getCrystalPosition();
        ItemStack crystalStack = inventoryHelper.getStackInSlot(PedestalContainer.SLOT_CRYSTAL);
        if (ItemStackTools.isValid(crystalStack)) {
            if (crystalStack.getItem() instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) (crystalStack.getItem());
                itemBlock.placeBlockAt(crystalStack, FakePlayerFactory.getMinecraft((WorldServer) getWorld()), getWorld(), pos, null, 0, 0, 0, itemBlock.getBlock().getStateFromMeta(0));
                inventoryHelper.decrStackSize(PedestalContainer.SLOT_CRYSTAL, 1);
                SoundTools.playSound(getWorld(), ModBlocks.resonatingCrystalBlock.getSoundType().breakSound, getPos().getX(), getPos().getY(), getPos().getZ(), 1.0f, 1.0f);

                if (findCollector()) {
                    TileEntity tileEntity = WorldHelper.getTileAt(getWorld(), new BlockPos(cachedLocator));
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

    private void checkCrystal() {
        BlockPos p = getCrystalPosition();
        Optional<Boolean> powerLow = getCrystal().map(tile -> tile.getPower() <= EnergyCollectorTileEntity.CRYSTAL_MIN_POWER);
        if (powerLow.orElse(false)) {
            dropCrystal();
        }
    }

    public void dropCrystal() {
        Optional<ResonatingCrystalTileEntity> crystal = getCrystal();
        if (!crystal.isPresent()) {
            return;
        }
        ResonatingCrystalTileEntity resonatingCrystalTileEntity = crystal.get();
        BlockPos p = resonatingCrystalTileEntity.getPos();
        ItemStack spentCrystal = new ItemStack(ModBlocks.resonatingCrystalBlock, 1);
        NBTTagCompound tagCompound = new NBTTagCompound();
        resonatingCrystalTileEntity.writeToNBT(tagCompound);
        spentCrystal.setTagCompound(tagCompound);
        inventoryLocator.ejectStack(getWorld(), getPos(), spentCrystal, pos, directions);
        getWorld().setBlockToAir(p);
        SoundTools.playSound(getWorld(), ModBlocks.resonatingCrystalBlock.getSoundType().breakSound, p.getX(), p.getY(), p.getZ(), 1.0f, 1.0f);
    }

    private boolean findCollector() {
        BlockPos crystalLocation = getCrystalPosition();
        if (cachedLocator != null) {
            if (WorldHelper.getBlockAt(getWorld(), cachedLocator) == EnergyCollectorSetup.energyCollectorBlock) {
                return true;
            }
            cachedLocator = null;
        }

        float closestDistance = Float.MAX_VALUE;

        int yy = crystalLocation.getY(), xx = crystalLocation.getX(), zz = crystalLocation.getZ();
        for (int y = yy - ConfigMachines.Collector.maxVerticalCrystalDistance ; y <= yy + ConfigMachines.Collector.maxVerticalCrystalDistance ; y++) {
            if (y >= 0 && y < getWorld().getHeight()) {
                int maxhordist = ConfigMachines.Collector.maxHorizontalCrystalDistance;
                for (int x = xx - maxhordist; x <= xx + maxhordist; x++) {
                    for (int z = zz - maxhordist; z <= zz + maxhordist; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (WorldHelper.getBlockAt(getWorld(), pos) == EnergyCollectorSetup.energyCollectorBlock) {
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
        readBufferFromNBT(tagCompound, inventoryHelper);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        writeBufferToNBT(tagCompound, inventoryHelper);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] { PedestalContainer.SLOT_CRYSTAL };
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUsable(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() == Item.getItemFromBlock(ModBlocks.resonatingCrystalBlock);
    }

}
