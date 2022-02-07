package mcjty.deepresonance.modules.generator.data;

import mcjty.deepresonance.modules.generator.block.GeneratorPartTileEntity;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import net.minecraftforge.energy.IEnergyStorage;

public class NetworkEnergyStorage implements IEnergyStorage {

    private final GeneratorPartTileEntity part;

    public NetworkEnergyStorage(GeneratorPartTileEntity part) {
        this.part = part;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    public int consumeEnergy(int amount) {
        GeneratorBlob blob = part.getBlob();
        if (blob != null) {
            int energy = blob.getEnergy();
            if (amount > energy) {
                amount = energy;
            }
            energy -= amount;
            blob.setEnergy(energy);
            return amount;
        }
        return 0;
    }

    @Override
    public int getEnergyStored() {
        GeneratorBlob blob = part.getBlob();
        if (blob != null) {
            return blob.getEnergy();
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        GeneratorBlob blob = part.getBlob();
        if (blob != null) {
            return blob.getGeneratorBlocks() * GeneratorConfig.POWER_STORAGE_PER_BLOCK.get();
        }
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
