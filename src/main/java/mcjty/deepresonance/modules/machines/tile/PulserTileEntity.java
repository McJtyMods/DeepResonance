package mcjty.deepresonance.modules.machines.tile;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import elec332.core.api.info.IInfoDataAccessorBlock;
import elec332.core.api.info.IInfoProvider;
import elec332.core.api.info.IInformation;
import elec332.core.api.info.InfoMod;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.util.AbstractTileEntity;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by Elec332 on 27-7-2020
 */
@RegisteredTileEntity("pulser")
public class PulserTileEntity extends AbstractTileEntity implements ITickableTileEntity, IInfoProvider {

    private final GenericEnergyStorage storage = new GenericEnergyStorage(this, true, MachinesModule.pulserConfig.powerMaximum.get(), MachinesModule.pulserConfig.powerPerTickIn.get());
    private final LazyOptional<IEnergyStorage> power = LazyOptional.of(() -> storage);

    private final Set<TileEntityResonatingCrystal> crystals = Sets.newHashSet();
    private int checkCooldown = 0;
    private int pulsePower = 0;

    @Override
    public void tick() {
        if (Preconditions.checkNotNull(world).isRemote) {
            return;
        }
        if (checkCooldown > 0) {
            checkCooldown--;
        }
        int powerPulse = MachinesModule.pulserConfig.powerPerPulse.get();
        if (powerLevel > 0) {
            long power = storage.getEnergy();
            long powerTransfer = powerPulse * powerLevel;
            if (power >= powerTransfer) {
                storage.consumeEnergy(powerTransfer);
                pulsePower += powerTransfer;
                markDirtyQuick();
            }
        }

        powerPulse *= 15;
        if (pulsePower >= powerPulse) {
            pulsePower -= powerPulse;

            if (checkCooldown <= 0) {
                crystals.clear();
                int range = MachinesModule.pulserConfig.crystalRange.get();
                for (int x = -range; x < range + 1; x++) {
                    for (int y = -range; y < range + 1; y++) {
                        for (int z = -range; z < range + 1; z++) {
                            TileEntity tile = WorldHelper.getTileAt(world, pos.add(x, y, z));
                            if (tile instanceof TileEntityResonatingCrystal) {
                                crystals.add((TileEntityResonatingCrystal) tile);
                            }
                        }
                    }
                }
                checkCooldown = 60; //3 sec
            }
            crystals.forEach(TileEntityResonatingCrystal::pulse);
            markDirtyQuick();
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        pulsePower = tagCompound.getInt("pulsePower");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putInt("pulsePower", pulsePower);
        return super.write(tagCompound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return CapabilityEnergy.ENERGY.orEmpty(cap, power);
    }

    @Override
    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
        if (information.getProviderType() == InfoMod.TOP) {
            int rfPulse = MachinesModule.pulserConfig.powerPerPulse.get() * 15;
            IProbeInfo probeInfo = (IProbeInfo) information.getInformationComponent();
            probeInfo.progress(Math.min(hitData.getData().getInt("pulsePower"), rfPulse), rfPulse);
        }
    }

    @Override
    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
        tag.putInt("pulsePower", pulsePower);
    }

}
