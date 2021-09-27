package mcjty.deepresonance.modules.generator.tile;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 30-7-2020
 */
public class TileEntityGeneratorPart extends AbstractTileEntityGeneratorComponent {

    private final Map<Direction, LazyOptional<IEnergyStorage>> surroundings = Maps.newEnumMap(Direction.class);
    private int storedEnergy;

    public int getStoredEnergy() {
        return storedEnergy;
    }

    public void setStoredEnergy(int storedEnergy) {
        this.storedEnergy = storedEnergy;
    }

    public int getSupportedCrystalPower() {
        return GeneratorModule.generatorConfig.maxPowerInputPerBlock.get();
    }

    public int getSupportedCrystals() {
        return GeneratorModule.generatorConfig.maxCrystalsPerBlock.get();
    }

    public int getMaximumPowerStored() {
        return GeneratorModule.generatorConfig.powerStoragePerBlock.get();
    }

    public int getMaximumPowerDistributed() {
        return GeneratorModule.generatorConfig.powerPerTickOut.get();
    }

    public TileEntityGeneratorPart() {
        super(GeneratorModule.TYPE_GENERATOR_PART.get());
    }

    public void distributeEnergy(Consumer<IEnergyStorage> distributor) {
        surroundings.values().forEach(cap -> {
            if (cap == null) {
                return;
            }
            cap.ifPresent(consumer -> {
                if (consumer.canReceive()) {
                    distributor.accept(consumer);
                }
            });
        });
    }

    @Override
    protected void writeEnergyCap(CompoundNBT tagCompound) {
    }

    @Override
    protected void readEnergyCap(CompoundNBT tagCompound) {
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        if (grid != null) {
            grid.writeData(this);
        }
        tagCompound.putInt("storedEnergy", storedEnergy);
        return super.write(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        storedEnergy = tagCompound.getInt("storedEnergy");
    }

    @Override
    public void validate() {
        super.validate();
        if (Preconditions.checkNotNull(getLevel()).isRemote) {
            return;
        }
        ElecCore.tickHandler.registerCall(() -> {
            surroundings.clear();
            for (Direction dir : Direction.values()) {
                BlockPos pos = getPos().offset(dir);
                TileEntity tile = WorldHelper.getTileAt(getLevel(), pos);
                if (tile != null) {
                    surroundings.put(dir, tile.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()));
                }
            }
        }, getLevel());
    }

    @Override
    public void onNeighborChange(BlockState myState, BlockPos neighbor) {
        if (Preconditions.checkNotNull(getLevel()).isRemote) {
            return;
        }
        BlockPos offset = getPos().subtract(neighbor);
        Direction side = Preconditions.checkNotNull(Direction.byLong(offset.getX(), offset.getY(), offset.getZ()));
        LazyOptional<IEnergyStorage> cap = surroundings.get(side);
        if (cap == null || !cap.isPresent()) {
            cap = null;
            TileEntity tile = WorldHelper.getTileAt(getLevel(), neighbor);
            if (tile != null) {
                cap = tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
            }
            surroundings.put(side, cap);
        }
    }

}
