package mcjty.deepresonance.util;

import elec332.core.inventory.BasicItemHandler;
import elec332.core.world.WorldHelper;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * Created by Elec332 on 7-1-2020
 */
public abstract class AbstractTileEntity extends GenericTileEntity implements RegisteredContainer.Modifier {

    protected final BasicItemHandler itemHandler;
    private final LazyOptional<IItemHandler> inventory;
    private final LazyOptional<INamedContainerProvider> screenHandler;

    public AbstractTileEntity(TileEntityType<?> type) {
        this(type, null);
    }

    public AbstractTileEntity(TileEntityType<?> type, BasicItemHandler itemHandler) {
        super(type);
        this.itemHandler = itemHandler;
        this.inventory = LazyOptional.of(() -> this.itemHandler);
        LazyOptional<INamedContainerProvider> screenHandler = createScreenHandler();
        if (screenHandler != null && !screenHandler.isPresent()) {
            screenHandler = null;
        }
        this.screenHandler = screenHandler;
    }

    @Nullable
    protected LazyOptional<INamedContainerProvider> createScreenHandler() {
        return null;
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        if (itemHandler != null) {
            itemHandler.writeToNBT(tagCompound);
        }

        return super.write(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        if (itemHandler != null) {
            itemHandler.deserializeNBT(tagCompound);
        }
    }

    public void onNeighborChange(BlockState myState, BlockPos neighbor) {
    }

    @Override
    public void onReplaced(World world, BlockPos pos, BlockState state, BlockState newState) {
        if (itemHandler == null) {
            super.onReplaced(world, pos, state, newState);
            return;
        }
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity == this) {
                dropInventory();
                world.updateComparatorOutputLevel(pos, newState.getBlock());
            }
            super.onReplaced(world, pos, state, newState);
        }
    }

    protected void dropInventory() {
        WorldHelper.dropInventoryItems(getWorld(), getPos(), itemHandler);
        itemHandler.clear();
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        if (itemHandler != null) {
            inventory.invalidate();
        }
        if (screenHandler != null) {
            screenHandler.invalidate();
        }
    }

    @Override
    public void modify(DefaultContainerProvider<GenericContainer> container) {
        if (itemHandler != null) {
            container.itemHandler(() -> itemHandler);
        }
    }

    public static IntReferenceHolder syncValue(IntSupplier getter, IntConsumer setter) {
        return new IntReferenceHolder() {

            @Override
            public int get() {
                return getter.getAsInt();
            }

            @Override
            public void set(int val) {
                setter.accept(val);
            }

        };
    }

    @Override
    protected void readItemHandlerCap(CompoundNBT tagCompound) {
    }

    @Override
    protected void writeItemHandlerCap(CompoundNBT tagCompound) {
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (itemHandler != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, inventory);
        }
        if (screenHandler != null && cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY.orEmpty(cap, screenHandler);
        }
        return super.getCapability(cap, side);
    }

}
