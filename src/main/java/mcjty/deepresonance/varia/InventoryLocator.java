package mcjty.deepresonance.varia;

import mcjty.lib.container.InventoryHelper;
import mcjty.lib.varia.Coordinate;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class InventoryLocator {
    private Coordinate inventoryCoordinate = null;
    private int inventorySide = 0;

    public IInventory getInventoryAtDirection(World worldObj, Coordinate thisCoordinate, ForgeDirection direction) {
        if (direction == ForgeDirection.UNKNOWN) {
            if (inventoryCoordinate != null) {
                return getInventoryAtCoordinate(worldObj, inventoryCoordinate);
            }
            return null;
        }
        // Remember in inventoryCoordinate (acts as a cache)
        inventoryCoordinate = thisCoordinate.addDirection(direction);
        inventorySide = direction.getOpposite().ordinal();
        return getInventoryAtCoordinate(worldObj, inventoryCoordinate);
    }

    private IInventory getInventoryAtCoordinate(World worldObj, Coordinate c) {
        TileEntity te = worldObj.getTileEntity(c.getX(), c.getY(), c.getZ());
        if (te instanceof IInventory) {
            return (IInventory) te;
        }
        return null;
    }

    public void ejectStack(World worldObj, int x, int y, int z, ItemStack spentMaterial, Coordinate thisCoordinate, ForgeDirection[] directions) {
        boolean spawnInWorld = true;
        for (ForgeDirection dir : directions) {
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


    public int getInventorySide() {
        return inventorySide;
    }
}
