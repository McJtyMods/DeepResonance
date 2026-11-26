package mcjty.deepresonance.modules.generator.data;

import mcjty.deepresonance.modules.generator.block.GeneratorPartTileEntity;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import team.reborn.energy.api.EnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class NetworkEnergyStorage implements EnergyStorage {

    private final GeneratorPartTileEntity part;

    public NetworkEnergyStorage(GeneratorPartTileEntity part) {
        this.part = part;
    }

    @Override
    public long insert(long amount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long extract(long amount, TransactionContext transaction) {
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

    public int getEnergyStored() {
        GeneratorBlob blob = part.getBlob();
        if (blob != null) {
            return blob.getEnergy();
        }
        return 0;
    }

    @Override
    public long getCapacity() {
        GeneratorBlob blob = part.getBlob();
        if (blob != null) {
            return blob.getGeneratorBlocks() * GeneratorConfig.POWER_STORAGE_PER_BLOCK.get();
        }
        return 0;
    }

    @Override
    public long getAmount() {
        return getEnergyStored();
    }
}
