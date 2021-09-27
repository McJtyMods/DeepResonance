package mcjty.deepresonance.modules.pulser.tile;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import elec332.core.api.info.IInfoDataAccessorBlock;
import elec332.core.api.info.IInfoProvider;
import elec332.core.api.info.IInformation;
import elec332.core.api.info.InfoMod;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.modules.pulser.PulserModule;
import mcjty.deepresonance.modules.pulser.util.PulserCapability;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by Elec332 on 27-7-2020
 */
public class TileEntityPulser extends AbstractPoweredTileEntity implements ITickableTileEntity, IInfoProvider {

    private final Set<LazyOptional<PulserCapability>> crystals = Sets.newHashSet();
    private int checkCooldown = 0;
    private int pulsePower = 0;

    public TileEntityPulser() {
        super(PulserModule.TYPE_PULSER.get(), PulserModule.pulserBlockConfig.powerMaximum.get(), PulserModule.pulserBlockConfig.powerPerTickIn.get());
    }

    @Override
    public void tick() {
        if (Preconditions.checkNotNull(world).isRemote) {
            return;
        }
        if (checkCooldown > 0) {
            checkCooldown--;
        }
        int powerPulse = PulserModule.pulserBlockConfig.powerPerPulse.get();
        if (powerLevel > 0) {
            long power = energyHandler.getEnergy();
            long powerTransfer = powerPulse * powerLevel;
            if (power >= powerTransfer) {
                energyHandler.consumeEnergy(powerTransfer);
                pulsePower += powerTransfer;
                markDirtyQuick();
            }
        }

        powerPulse *= 15;
        if (pulsePower >= powerPulse) {
            pulsePower -= powerPulse;

            if (checkCooldown <= 0) {
                crystals.clear();
                int range = PulserModule.pulserBlockConfig.crystalRange.get();
                for (int x = -range; x < range + 1; x++) {
                    for (int y = -range; y < range + 1; y++) {
                        for (int z = -range; z < range + 1; z++) {
                            TileEntity tile = WorldHelper.getTileAt(world, pos.add(x, y, z));
                            if (tile instanceof TileEntityResonatingCrystal) {
                                crystals.add(tile.getCapability(PulserModule.PULSER_CAPABILITY, null));
                            }
                        }
                    }
                }
                checkCooldown = 100; //5 sec
            }
            crystals.forEach(cap -> cap.ifPresent(PulserCapability::pulse));
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

    @Override
    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
        if (information.getProviderType() == InfoMod.TOP) {
            int rfPulse = PulserModule.pulserBlockConfig.powerPerPulse.get() * 15;
            IProbeInfo probeInfo = (IProbeInfo) information.getInformationComponent();
            probeInfo.progress(Math.min(hitData.getData().getInt("pulsePower"), rfPulse), rfPulse);
        }
    }

    @Override
    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
        tag.putInt("pulsePower", pulsePower);
    }

}
