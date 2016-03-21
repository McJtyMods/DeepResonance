package mcjty.deepresonance.varia;

import elec332.core.world.WorldHelper;
import mcjty.lib.container.InventoryHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class InventoryLocator {

    private BlockPos inventoryCoordinate = null;
    private EnumFacing inventorySide = null;

    public IItemHandler getItemHandlerAtDirection(World worldObj, BlockPos thisCoordinate, EnumFacing direction) {
        if (direction == null) {
            if (inventoryCoordinate != null) {
                return getItemHandlerAtCoordinate(worldObj, inventoryCoordinate, inventorySide);
            }
            return null;
        }
        TileEntity te = WorldHelper.getTileAt(worldObj, thisCoordinate);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite())) {
            // Remember in inventoryCoordinate (acts as a cache)
            inventoryCoordinate = thisCoordinate.offset(direction);
            inventorySide = direction.getOpposite();
            return getItemHandlerAtCoordinate(worldObj, inventoryCoordinate, inventorySide);
        }
        return null;
    }

    private IItemHandler getItemHandlerAtCoordinate(World worldObj, BlockPos c, EnumFacing direction) {
        TileEntity te = WorldHelper.getTileAt(worldObj, c);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction)) {
            return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
        }
        return null;
    }


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
        for (EnumFacing dir : directions) {
            IItemHandler handler = getItemHandlerAtDirection(worldObj, thisCoordinate, dir);
            if (spentMaterial == null) {
                break;
            }

            if (handler != null) {
                spentMaterial = ItemHandlerHelper.insertItem(handler, spentMaterial, false);
            } else {
                IInventory inventory = getInventoryAtDirection(worldObj, thisCoordinate, dir);
                if (inventory != null) {
                    int amount = InventoryHelper.mergeItemStackSafe(inventory, false, getInventorySide(), spentMaterial, 0, inventory.getSizeInventory(), null);
                    if (amount == 0) {
                        spentMaterial = null;
                    } else {
                        spentMaterial.stackSize = amount;
                    }
                }
            }
        }

        if (spentMaterial != null) {
            EntityItem entityItem = new EntityItem(worldObj, x, y, z, spentMaterial);
            worldObj.spawnEntityInWorld(entityItem);
        }
    }


    public EnumFacing getInventorySide() {
        return inventorySide;
    }

}
