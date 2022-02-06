package mcjty.deepresonance.modules.generator.data;

import mcjty.lib.multiblock.IMultiblock;
import net.minecraft.nbt.CompoundTag;

public class GeneratorBlob implements IMultiblock {
    private int generatorBlocks = 0;
    private int collectorBlocks = -1;       // Invalid
    private int energy;
    private boolean active;
    private int startupCounter;
    private int shutdownCounter;
    private int lastRfPerTick;

    public GeneratorBlob() {
    }

    public GeneratorBlob(GeneratorBlob other) {
        this.generatorBlocks = other.generatorBlocks;
        this.collectorBlocks = other.collectorBlocks;
        this.energy = other.energy;
        this.active = other.active;
        this.startupCounter = other.startupCounter;
        this.shutdownCounter = other.shutdownCounter;
        this.lastRfPerTick = other.lastRfPerTick;
    }

    public void merge(GeneratorBlob blob) {
        generatorBlocks += blob.generatorBlocks;
        collectorBlocks = -1;
        energy += blob.energy;
    }

    public GeneratorBlob setGeneratorBlocks(int generatorBlocks) {
        this.generatorBlocks = generatorBlocks;
        return this;
    }

    public GeneratorBlob addGeneratorBlocks(int g) {
        this.generatorBlocks += g;
        return this;
    }

    public int getGeneratorBlocks() {
        return generatorBlocks;
    }

    public GeneratorBlob setCollectorBlocks(int collectorBlocks) {
        this.collectorBlocks = collectorBlocks;
        return this;
    }

    public int getCollectorBlocks() {
        return collectorBlocks;
    }

    public int getEnergy() {
        return energy;
    }

    public GeneratorBlob setEnergy(int energy) {
        this.energy = energy;
        return this;
    }

    public GeneratorBlob addEnergy(int e) {
        this.energy += e;
        return this;
    }

    public int getLastRfPerTick() {
        return lastRfPerTick;
    }

    public GeneratorBlob setLastRfPerTick(int lastRfPerTick) {
        this.lastRfPerTick = lastRfPerTick;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public GeneratorBlob setActive(boolean active) {
        this.active = active;
        return this;
    }

    public int getStartupCounter() {
        return startupCounter;
    }

    public GeneratorBlob setStartupCounter(int startupCounter) {
        this.startupCounter = startupCounter;
        return this;
    }

    public int getShutdownCounter() {
        return shutdownCounter;
    }

    public GeneratorBlob setShutdownCounter(int shutdownCounter) {
        this.shutdownCounter = shutdownCounter;
        return this;
    }

    public static GeneratorBlob load(CompoundTag tagCompound) {
        return new GeneratorBlob()
                .setGeneratorBlocks(tagCompound.getInt("refcount"))
                .setCollectorBlocks(tagCompound.getInt("collectors"))
                .setEnergy(tagCompound.getInt("energy"))
                .setActive(tagCompound.getBoolean("active"))
                .setStartupCounter(tagCompound.getInt("startup"))
                .setShutdownCounter(tagCompound.getInt("shutdown"));
    }

    public static CompoundTag save(CompoundTag tagCompound, GeneratorBlob network) {
        tagCompound.putInt("refcount", network.generatorBlocks);
        tagCompound.putInt("collectors", network.collectorBlocks);
        tagCompound.putInt("energy", network.energy);
        tagCompound.putBoolean("active", network.active);
        tagCompound.putInt("startup", network.startupCounter);
        tagCompound.putInt("shutdown", network.shutdownCounter);
        return tagCompound;
    }
}
