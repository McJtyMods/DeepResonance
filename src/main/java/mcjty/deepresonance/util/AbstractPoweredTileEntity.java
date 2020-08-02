package mcjty.deepresonance.util;

import elec332.core.inventory.BasicItemHandler;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 29-7-2020
 */
public abstract class AbstractPoweredTileEntity extends AbstractTileEntity implements RegisteredContainer.Modifier {

    protected final GenericEnergyStorage energyHandler;
    private final LazyOptional<IEnergyStorage> power;

    public AbstractPoweredTileEntity(int powerMax, int powerPerTickIn) {
        this(powerMax, powerPerTickIn, null);
    }

    public AbstractPoweredTileEntity(int powerMax, int powerPerTickIn, BasicItemHandler itemHandler) {
        super(itemHandler);
        this.energyHandler = new GenericEnergyStorage(this, true, powerMax, powerPerTickIn);
        this.power = LazyOptional.of(() -> this.energyHandler);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        power.invalidate();
    }

    @Override
    public void modify(DefaultContainerProvider<GenericContainer> container) {
        super.modify(container);
        container.energyHandler(() -> energyHandler);
        container.shortListener(syncValue(energyHandler::getEnergyStored, energyHandler::setEnergy));
    }

    public int getMaxPower() {
        return energyHandler.getMaxEnergyStored();
    }

    public int getCurrentPower() {
        return energyHandler.getEnergyStored();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.orEmpty(cap, power);
        }
        return super.getCapability(cap, side);
    }

}
