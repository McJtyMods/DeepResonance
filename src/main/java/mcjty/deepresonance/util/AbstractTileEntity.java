package mcjty.deepresonance.util;

import com.google.common.base.Preconditions;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.handler.annotations.TileEntityAnnotationProcessor;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

/**
 * Created by Elec332 on 7-1-2020
 */
public abstract class AbstractTileEntity extends GenericTileEntity implements RegisteredTileEntity.TypeSetter {

    private TileEntityType<?> type;

    public AbstractTileEntity() {
        super(null);
        this.setTileEntityType(TileEntityAnnotationProcessor.getTileType(this.getClass()));
    }

    public AbstractTileEntity(TileEntityType<?> type) {
        super(type);
        this.type = type;
    }

    public void dropInventory(IItemHandler inv) {
        World world = Preconditions.checkNotNull(getWorld());
        IntStream.range(0, inv.getSlots())
                .mapToObj(inv::getStackInSlot)
                .forEach(stack -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
    }

    @Override
    protected void readItemHandlerCap(CompoundNBT tagCompound) {
    }

    @Override
    protected void writeItemHandlerCap(CompoundNBT tagCompound) {
    }

    @Override
    public void setTileEntityType(TileEntityType<?> type) {
        this.type = type;
    }

    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return this.type;
    }

}
