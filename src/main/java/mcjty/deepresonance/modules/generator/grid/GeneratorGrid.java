package mcjty.deepresonance.modules.generator.grid;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.grid.ITileEntityLink;
import elec332.core.grid.multiblock.AbstractDynamicMultiblock;
import elec332.core.grid.multiblock.SimpleDynamicMultiblockTileLink;
import elec332.core.world.DimensionCoordinate;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.tile.AbstractTileEntityGeneratorComponent;
import mcjty.deepresonance.modules.generator.tile.TileEntityEnergyCollector;
import mcjty.deepresonance.modules.generator.tile.TileEntityGeneratorController;
import mcjty.deepresonance.modules.generator.tile.TileEntityGeneratorPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by Elec332 on 30-7-2020
 */
public class GeneratorGrid extends AbstractDynamicMultiblock<AbstractTileEntityGeneratorComponent, GeneratorGrid, SimpleDynamicMultiblockTileLink<AbstractTileEntityGeneratorComponent, GeneratorGrid>> implements IEnergyStorage, ICapabilityProvider {

    public GeneratorGrid() {
        this.capacities = Maps.newHashMap();
    }

    private final Map<DimensionCoordinate, Integer> capacities;
    private int capacity = 0;
    private int energyStored = 0;
    private int oldCapacity = 0;
    private LazyOptional<IEnergyStorage> energyCapability;

    private int supportedCrystals = 0;
    private int crystalPowerIn = 0;
    private boolean hasDuplicates = false;
    private ITileEntityLink controller = null;
    private ITileEntityLink collector = null;
    private int startup = -1;
    private boolean redstone = false;

    private boolean needsOnSync = true;

    @Override
    public boolean canMerge(GeneratorGrid other) {
        return true;
    }

    @Override
    protected void onMergedWith(GeneratorGrid other) {
        this.energyStored += other.energyStored;
    }

    @Override
    protected void onComponentAdded(SimpleDynamicMultiblockTileLink<AbstractTileEntityGeneratorComponent, GeneratorGrid> link, boolean mergeAdd) {
        AbstractTileEntityGeneratorComponent tile = link.getTileEntity();
        if (tile == null) {
            return;
        }
        if (tile instanceof TileEntityGeneratorPart) {
            TileEntityGeneratorPart gen = (TileEntityGeneratorPart) tile;
            int power = gen.getMaximumPowerStored();
            capacity += power;
            capacities.put(link.getPosition(), power);

            if (!mergeAdd) {
                energyStored += gen.getStoredEnergy();
                gen.setStoredEnergy(0);
            }

            supportedCrystals += gen.getSupportedCrystals();
            crystalPowerIn += gen.getSupportedCrystalPower();
        } else if (tile instanceof TileEntityEnergyCollector) {
            if (collector != null) {
                hasDuplicates = true;
            } else {
                collector = link;
            }
        } else if (tile instanceof TileEntityGeneratorController) {
            if (controller != null) {
                hasDuplicates = true;
            } else {
                controller = link;
                setStartup(tile.getStartupTimer());
            }
        }
        if (redstone && controller != null && collector != null) {
            needsOnSync = true;
        }
    }

    @Override
    public void onComponentRemoved(SimpleDynamicMultiblockTileLink<AbstractTileEntityGeneratorComponent, GeneratorGrid> link) {
        TileEntity tile = link.getTileEntity();
        if (tile != null) {
            DimensionCoordinate coord = DimensionCoordinate.fromTileEntity(tile);
            if (capacities.containsKey(coord)) {
                float share;
                if (capacities.size() == 1) {
                    share = 1;
                } else {
                    share = capacities.get(coord) / (float) capacity;
                }
                capacities.remove(coord);
                int energy = (int) Math.floor(share * energyStored);
                writeData((TileEntityGeneratorPart) tile, energy);
            }
            if (controller != null && controller.getPosition().equals(coord)) {
                ((TileEntityGeneratorController) tile).setStartupTimer(startup);
            }
        }
        super.onComponentRemoved(link);
    }

    @Override
    protected void onGridChanged() {
        if (hasDuplicates || controller == null) {
            if (energyCapability != null) {
                energyCapability.invalidate();
            }
            energyCapability = LazyOptional.empty();
            return;
        }
        if (oldCapacity != capacity) {
            if (energyCapability != null) {
                energyCapability.invalidate();
            }
            energyCapability = LazyOptional.of(() -> this);
            oldCapacity = capacity;
        }
    }

    @Override
    public void tick() {
        if (needsOnSync) {
            needsOnSync = false;
            syncRedstone();
        }
        if (controller == null || startup < 0 || hasDuplicates) {
            return;
        }
        TileEntityGeneratorController controller = (TileEntityGeneratorController) this.controller.getTileEntity();
        if (controller == null) {
            return;
        }
        if (collector == null) {
            return;
        }
        TileEntityEnergyCollector collector = (TileEntityEnergyCollector) this.collector.getTileEntity();
        if (collector == null) {
            return;
        }
        if (startup > 0) {
            setStartup(startup - 1);
            if (startup == 0) {
                collector.updateCrystals();
            }
        }
        if (startup == 0) {
            energyStored = Math.min(capacity, energyStored + collector.collectPower());
            for (ITileEntityLink link : getComponents()) {
                TileEntity tile = link.getTileEntity();
                if (tile instanceof TileEntityGeneratorPart) {
                    int maxout = Math.min(energyStored, ((TileEntityGeneratorPart) tile).getMaximumPowerDistributed());
                    IntReferenceHolder ref = IntReferenceHolder.single();
                    ref.set(maxout);
                    ((TileEntityGeneratorPart) tile).distributeEnergy(energyObj -> {
                        int max = ref.get();
                        if (max > 0) {
                            ref.set(max - energyObj.receiveEnergy(max, false));
                        }
                    });
                    energyStored -= (maxout - ref.get());
                }
            }
        }
    }

    public void onRedstoneChanged(boolean hasRedstone) {
        if (hasRedstone == redstone) {
            return;
        }
        redstone = hasRedstone;
        if (collector == null || controller == null) {
            return;
        }
        syncRedstone();
    }

    private void syncRedstone() {
        if (redstone) {
            if (startup >= 0) {
                return;
            }
            setStartup(GeneratorModule.generatorConfig.startupTime.get());
        } else {
            setStartup(-1);
        }
        getComponents().forEach(c -> {
            AbstractTileEntityGeneratorComponent tile = c.getTileEntity();
            if (tile != null) {
                tile.generatorTurnedOn(startup >= 0);
            }
        });
    }

    private void setStartup(int startup) {
        this.startup = startup;
        if (collector != null) {
            TileEntityEnergyCollector collector = (TileEntityEnergyCollector) this.collector.getTileEntity();
            if (collector != null) {
                collector.setStartupTimer(startup);
            }
        }
        Preconditions.checkNotNull(((TileEntityGeneratorController) controller.getTileEntity())).setStartupTimer(startup);
    }

    public void writeData(TileEntity tile) {
        DimensionCoordinate coord = DimensionCoordinate.fromTileEntity(tile);
        if (!capacities.containsKey(coord)) {
            return;
        }
        float share = capacities.get(coord);
        share /= capacity;
        writeData(((TileEntityGeneratorPart) tile), (int) Math.floor(share * energyStored));
    }

    private void writeData(TileEntityGeneratorPart tile, int energy) {
        tile.setStoredEnergy(energy);
        if (WorldHelper.chunkLoaded(tile.getWorld(), tile.getPos())) {
            tile.markDirty();
        }
    }

    public int getMaxSupportedCrystals() {
        return supportedCrystals;
    }

    public int getMaxPowerCollected() {
        return crystalPowerIn;
    }

    public boolean hasDuplicates() {
        return hasDuplicates;
    }

    public String getStartupText() {
        if (startup > 0) {
            return " Startup " + (100 - (int) ((startup / (float) GeneratorModule.generatorConfig.startupTime.get()) * 100)) + "%";
        }
        return startup == -1 ? "Off" : "On";
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CapabilityEnergy.ENERGY.orEmpty(cap, energyCapability);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(energyStored, maxExtract);
        if (!simulate) {
            energyStored -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energyStored;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

}
