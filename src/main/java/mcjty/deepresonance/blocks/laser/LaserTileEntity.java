package mcjty.deepresonance.blocks.laser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalBlock;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.blocks.lens.LensSetup;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericEnergyReceiverTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.network.PacketRequestIntegerFromServer;
import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.Coordinate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class LaserTileEntity extends GenericEnergyReceiverTileEntity implements ISidedInventory {

    public static final String CMD_GETLIQUID = "getLiquid";
    public static final String CLIENTCMD_GETLIQUID = "getLiquid";

    public static final int COLOR_BLUE = 1;
    public static final int COLOR_RED = 2;
    public static final int COLOR_GREEN = 3;
    public static final int COLOR_YELLOW = 4;

    // Transient
    private int tickCounter = 10;

    private int progressCounter = 0;
    private int color = 0;          // 0 means not active, > 0 means a color laser
    private int crystalLiquid = 0;  // This is not RCL but just liquidified spent crystal
    private int powered = 0;

    private static int crystalLiquidClient = 0;

    // Infusing bonus for items. Index is the registry name of the item.
    public static Map<String, InfusingBonus> infusingBonusMap = null;

    private InventoryHelper inventoryHelper = new InventoryHelper(this, LaserContainer.factory, 2);

    public LaserTileEntity() {
        super(ConfigMachines.Laser.rfMaximum, ConfigMachines.Laser.rfPerTick);
    }

    @Override
    public void setPowered(int powered) {
        if (this.powered != powered) {
            this.powered = powered;
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    protected void checkStateServer() {
        tickCounter--;
        if (tickCounter > 0) {
            return;
        }
        tickCounter = 10;

        checkCrystal();

        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

        if (powered == 0) {
            changeColor(0, meta);
            return;
        }

        ItemStack stack = inventoryHelper.getStackInSlot(LaserContainer.SLOT_CATALYST);
        InfusingBonus bonus = getInfusingBonus(stack);
        if (bonus == null) {
            changeColor(0, meta);
            return;
        }

        if (getEnergyStored(ForgeDirection.UNKNOWN) < ConfigMachines.Laser.rfUsePerCatalyst) {
            changeColor(0, meta);
            return;
        }

        if (crystalLiquid < ConfigMachines.Laser.crystalLiquidPerCatalyst) {
            changeColor(0, meta);
            return;
        }

        Coordinate tankCoordinate = findLens(meta);
        if (tankCoordinate != null) {
            changeColor(bonus.getColor(), meta);
        } else {
            changeColor(0, meta);
            return;
        }

        progressCounter--;
        markDirty();
        if (progressCounter > 0) {
            return;
        }
        progressCounter = ConfigMachines.Laser.ticks10PerCatalyst;

        infuseLiquid(tankCoordinate, bonus);
    }

    private boolean validRCLTank(TileTank tank){
        Fluid fluid = DRFluidRegistry.getFluidFromStack(tank.getFluid());
        return fluid == null || fluid == DRFluidRegistry.liquidCrystal;
    }


    private void infuseLiquid(Coordinate tankCoordinate, InfusingBonus bonus) {
        // We consume stuff even if the tank does not have enough liquid. Player has to be careful
        decrStackSize(LaserContainer.SLOT_CATALYST, 1);
        consumeEnergy(ConfigMachines.Laser.rfUsePerCatalyst);
        crystalLiquid -= ConfigMachines.Laser.crystalLiquidPerCatalyst;

        TileEntity te = worldObj.getTileEntity(tankCoordinate.getX(), tankCoordinate.getY(), tankCoordinate.getZ());
        if (te instanceof TileTank) {
            TileTank tileTank = (TileTank) te;
            if (validRCLTank(tileTank)) {
                FluidStack stack = tileTank.drain(ForgeDirection.UNKNOWN, ConfigMachines.Laser.rclPerCatalyst, false);
                if (stack != null) {
                    stack = tileTank.drain(ForgeDirection.UNKNOWN, ConfigMachines.Laser.rclPerCatalyst, true);
                    LiquidCrystalFluidTagData fluidData = LiquidCrystalFluidTagData.fromStack(stack);
                    float purity = bonus.getPurityModifier().modify(fluidData.getPurity(), fluidData.getQuality());
                    float strength = bonus.getStrengthModifier().modify(fluidData.getStrength(), fluidData.getQuality());
                    float efficiency = bonus.getEfficiencyModifier().modify(fluidData.getEfficiency(), fluidData.getQuality());
                    fluidData.setPurity(purity);
                    fluidData.setStrength(strength);
                    fluidData.setEfficiency(efficiency);
                    FluidStack newStack = fluidData.makeLiquidCrystalStack();
                    if (Math.abs(purity) < 0.01) {
                        newStack.amount /= 10;
                    }
                    tileTank.fill(ForgeDirection.UNKNOWN, newStack, true);
                }
            }
        }
    }

    private void changeColor(int newcolor, int meta) {
        if (newcolor != color) {
            color = newcolor;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            // We only have four bits so we can't represent yellow. We use red in that case.
            if (color == COLOR_YELLOW) {
                meta = (meta & 0x3) | (COLOR_RED<<2);
            } else {
                meta = (meta & 0x3) | (color<<2);
            }
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 3);
            markDirty();
        }
    }

    public int getColor() {
        return color;
    }

    private void checkCrystal() {
        ItemStack stack = inventoryHelper.getStackInSlot(LaserContainer.SLOT_CRYSTAL);
        if (stack != null) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            float strength = tagCompound == null ? 0 : tagCompound.getFloat("strength") / 100.0f;
            int addAmount = (int) (ConfigMachines.Laser.minCrystalLiquidPerCrystal + strength * (ConfigMachines.Laser.maxCrystalLiquidPerCrystal - ConfigMachines.Laser.minCrystalLiquidPerCrystal));
            int newAmount = crystalLiquid + addAmount;
            if (newAmount > ConfigMachines.Laser.crystalLiquidMaximum) {
                // Not enough room
                return;
            }
            inventoryHelper.decrStackSize(LaserContainer.SLOT_CRYSTAL, 1);
            crystalLiquid = newAmount;
            markDirty();
        }
    }

    public static InfusingBonus getInfusingBonus(ItemStack item) {
        if (item == null) {
            return null;
        }
        String name = Item.itemRegistry.getNameForObject(item.getItem());
        return infusingBonusMap.get(name);
    }

    public static void createDefaultInfusionBonusMap() {
        infusingBonusMap = new HashMap<String, InfusingBonus>();
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.diamond), new InfusingBonus(
                COLOR_BLUE,
                new InfusingBonus.Modifier(5.0f, 100.0f),
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.emerald), new InfusingBonus(
                COLOR_GREEN,
                new InfusingBonus.Modifier(8.0f, 100.0f),
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.ender_pearl), new InfusingBonus(
                COLOR_GREEN,
                new InfusingBonus.Modifier(2.0f, 100.0f),
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.redstone), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-1.0f, 0.0f),
                new InfusingBonus.Modifier(5.0f, 60.0f),
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.gunpowder), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-5.0f, 0.0f),
                new InfusingBonus.Modifier(8.0f, 70.0f),
                new InfusingBonus.Modifier(4.0f, 60.0f)));
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.glowstone_dust), new InfusingBonus(
                COLOR_YELLOW,
                new InfusingBonus.Modifier(-2.0f, 0.0f),
                new InfusingBonus.Modifier(6.0f, 50.0f),
                new InfusingBonus.Modifier(3.0f, 50.0f)));
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.blaze_powder), new InfusingBonus(
                COLOR_YELLOW,
                new InfusingBonus.Modifier(-6.0f, 0.0f),
                new InfusingBonus.Modifier(5.0f, 70.0f),
                new InfusingBonus.Modifier(5.0f, 70.0f)));
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.quartz), new InfusingBonus(
                COLOR_BLUE,
                new InfusingBonus.Modifier(1.0f, 0.0f),
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(7.0f, 80.0f)));
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.nether_star), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-60.0f, 0.0f),
                new InfusingBonus.Modifier(90.0f, 100.0f),
                new InfusingBonus.Modifier(90.0f, 100.0f)));
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.ghast_tear), new InfusingBonus(
                COLOR_YELLOW,
                new InfusingBonus.Modifier(-20.0f, 0.0f),
                new InfusingBonus.Modifier(25.0f, 100.0f),
                new InfusingBonus.Modifier(15.0f, 100.0f)));
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.slime_ball), new InfusingBonus(
                COLOR_GREEN,
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(-10.0f, 1.0f)));
        infusingBonusMap.put(Item.itemRegistry.getNameForObject(Items.coal), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-1.0f, 0.0f),
                new InfusingBonus.Modifier(-10.0f, 0.0f),
                InfusingBonus.Modifier.NONE));
    }

    private Coordinate findLens(int meta) {
        ForgeDirection direction = BlockTools.getOrientationHoriz(meta);
        Coordinate shouldBeAir = getCoordinate().addDirection(direction);
        if (!worldObj.isAirBlock(shouldBeAir.getX(), shouldBeAir.getY(), shouldBeAir.getZ())) {
            return null;
        }
        Coordinate shouldBeLens = shouldBeAir.addDirection(direction);
        Block lensBlock = worldObj.getBlock(shouldBeLens.getX(), shouldBeLens.getY(),shouldBeLens.getZ());
        if (lensBlock != LensSetup.lensBlock) {
            return null;
        }
        ForgeDirection lensDirection = BlockTools.getOrientationHoriz(worldObj.getBlockMetadata(shouldBeLens.getX(), shouldBeLens.getY(), shouldBeLens.getZ()));
        if (lensDirection != direction) {
            return null;
        }

        return shouldBeLens.addDirection(direction);
    }

    public void requestCrystalLiquidFromServer() {
        DeepResonance.networkHandler.getNetworkWrapper().sendToServer(new PacketRequestIntegerFromServer(xCoord, yCoord, zCoord,
                CMD_GETLIQUID,
                CLIENTCMD_GETLIQUID));
    }

    @SideOnly(Side.CLIENT)
    public static int getCrystalLiquidClient() {
        return crystalLiquidClient;
    }

    @Override
    public Integer executeWithResultInteger(String command, Map<String, Argument> args) {
        Integer rc = super.executeWithResultInteger(command, args);
        if (rc != null) {
            return rc;
        }
        if (CMD_GETLIQUID.equals(command)) {
            return crystalLiquid;
        }
        return null;
    }

    @Override
    public boolean execute(String command, Integer result) {
        boolean rc = super.execute(command, result);
        if (rc) {
            return true;
        }
        if (CLIENTCMD_GETLIQUID.equals(command)) {
            crystalLiquidClient = result;
            return true;
        }
        return false;
    }


    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        color = tagCompound.getInteger("color");
        progressCounter = tagCompound.getInteger("progress");
        powered = tagCompound.getByte("powered");
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        readBufferFromNBT(tagCompound);
        crystalLiquid = tagCompound.getInteger("liquid");
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
        tagCompound.setInteger("progress", progressCounter);
        tagCompound.setByte("powered", (byte) powered);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        writeBufferToNBT(tagCompound);
        tagCompound.setInteger("liquid", crystalLiquid);
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
        return AxisAlignedBB.getBoundingBox(xCoord - 3, yCoord - 1, zCoord - 3, xCoord + 4, yCoord + 2, zCoord + 4);
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
