package mcjty.deepresonance.varia;

import elec332.core.world.WorldHelper;
import mcjty.lib.container.InventoryHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class InventoryLocator {

    private BlockPos inventoryCoordinate = null;
    private EnumFacing inventorySide = null;

    public IInventory getInventoryAtDirection(World worldObj, BlockPos thisCoordinate, EnumFacing direction) {
        if (direction == null) {
            if (inventoryCoordinate != null) {
                return getInventoryAtCoordinate(worldObj, inventoryCoordinate);
            }
            return null;
        }
        // Remember in inventoryCoordinate (acts as a cache)
        inventoryCoordinate = thisCoordinate.offset(direction);
        inventorySide = direction.getOpposite();
        return getInventoryAtCoordinate(worldObj, inventoryCoordinate);
    }

    private IInventory getInventoryAtCoordinate(World worldObj, BlockPos c) {
        TileEntity te = WorldHelper.getTileAt(worldObj, c);
        if (te instanceof IInventory) {
            return (IInventory) te;
        }
        return null;
    }

    public void ejectStack(World worldObj, int x, int y, int z, ItemStack spentMaterial, BlockPos thisCoordinate, EnumFacing[] directions) {
        boolean spawnInWorld = true;
        for (EnumFacing dir : directions) {
            IInventory inventory = getInventoryAtDirection(worldObj, thisCoordinate, dir);
            if (inventory != null) {
                if (InventoryHelper.mergeItemStackSafe(inventory, false, getInventorySide(), spentMaterial, 0, inventory.getSizeInventory(), null) == 0) {
                    spawnInWorld = false;
                    break;
                }
            }
        }

        if (spawnInWorld) {
            EntityItem entityItem = new EntityItem(worldObj, x, y, z, spentMaterial);
            worldObj.spawnEntityInWorld(entityItem);
        }
    }


    public EnumFacing getInventorySide() {
        return inventorySide;
    }

}
