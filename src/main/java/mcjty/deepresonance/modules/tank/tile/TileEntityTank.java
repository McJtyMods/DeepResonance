package mcjty.deepresonance.modules.tank.tile;

import elec332.core.api.registration.HasSpecialRenderer;
import elec332.core.api.registration.RegisteredTileEntity;
import mcjty.deepresonance.modules.tank.client.TankTESR;
import mcjty.deepresonance.util.AbstractTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 7-1-2020
 */
@RegisteredTileEntity("tank")
@HasSpecialRenderer(TankTESR.class)
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
