package mcjty.deepresonance.tile;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 7-1-2020
 */
public class TileEntityTank extends AbstractTileEntity {

    private ICapabilityProvider grid;

    public void setGrid(ICapabilityProvider grid) {
        this.grid = grid;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return grid.getCapability(cap, side);
    }

}
