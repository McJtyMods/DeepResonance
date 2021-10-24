package mcjty.deepresonance.modules.generator.data;

import mcjty.lib.multiblock.IMultiblock;
import net.minecraft.nbt.CompoundNBT;

public class GeneratorBlob implements IMultiblock {
    private final int generatorBlocks;
    private int collectorBlocks;
    private int energy;
    private boolean active;
    private int startupCounter;
    private int shutdownCounter;
    private int lastRfPerTick;

    private GeneratorBlob(Builder builder) {
        this.generatorBlocks = builder.generatorBlocks;
        this.collectorBlocks = builder.collectorBlocks;
        this.energy = builder.energy;
        this.active = builder.active;
        this.startupCounter = builder.startupCounter;
        this.shutdownCounter = builder.shutdownCounter;
        this.lastRfPerTick = builder.lastRfPerTick;
    }

    public int getGeneratorBlocks() {
        return generatorBlocks;
    }

    public int getCollectorBlocks() {
        return collectorBlocks;
    }

    public void setCollectorBlocks(int collectorBlocks) {
        this.collectorBlocks = collectorBlocks;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getLastRfPerTick() {
        return lastRfPerTick;
    }

    public void setLastRfPerTick(int lastRfPerTick) {
        this.lastRfPerTick = lastRfPerTick;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getStartupCounter() {
        return startupCounter;
    }

    public void setStartupCounter(int startupCounter) {
        this.startupCounter = startupCounter;
    }

    public int getShutdownCounter() {
        return shutdownCounter;
    }

    public void setShutdownCounter(int shutdownCounter) {
        this.shutdownCounter = shutdownCounter;
    }

    public static GeneratorBlob load(CompoundNBT tagCompound) {
        return GeneratorBlob.builder()
                .generatorBlocks(tagCompound.getInt("refcount"))
                .collectorBlocks(tagCompound.getInt("collectors"))
                .energy(tagCompound.getInt("energy"))
                .active(tagCompound.getBoolean("active"))
                .startupCounter(tagCompound.getInt("startup"))
                .shutdownCounter(tagCompound.getInt("shutdown"))
                .build();
    }

    public static CompoundNBT save(CompoundNBT tagCompound, GeneratorBlob network) {
        tagCompound.putInt("refcount", network.generatorBlocks);
        tagCompound.putInt("collectors", network.collectorBlocks);
        tagCompound.putInt("energy", network.energy);
        tagCompound.putBoolean("active", network.active);
        tagCompound.putInt("startup", network.startupCounter);
        tagCompound.putInt("shutdown", network.shutdownCounter);
        return tagCompound;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int generatorBlocks = 0;
        private int collectorBlocks = -1;   // Invalid
        private int energy = 0;
        private boolean active;
        private int startupCounter;
        private int shutdownCounter;
        private int lastRfPerTick;

        public Builder network(GeneratorBlob blob) {
            generatorBlocks = blob.generatorBlocks;
            collectorBlocks = blob.collectorBlocks;
            energy = blob.energy;
            active = blob.active;
            startupCounter = blob.startupCounter;
            shutdownCounter = blob.shutdownCounter;
            lastRfPerTick = blob.lastRfPerTick;
            return this;
        }

        public Builder merge(GeneratorBlob blob) {
            generatorBlocks += blob.generatorBlocks;
            collectorBlocks += blob.collectorBlocks;
            energy += blob.energy;
            return this;
        }

        public Builder generatorBlocks(int generatorBlocks) {
            this.generatorBlocks = generatorBlocks;
            return this;
        }

        public Builder addGeneratorBlocks(int a) {
            this.generatorBlocks += a;
            return this;
        }

        public Builder collectorBlocks(int collectorBlocks) {
            this.collectorBlocks = collectorBlocks;
            return this;
        }

        public Builder addCollectorBlocks(int a) {
            this.collectorBlocks += a;
            return this;
        }

        public Builder energy(int energy) {
            this.energy = energy;
            return this;
        }

        public Builder addEnergy(int a) {
            this.energy += a;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder startupCounter(int startupCounter) {
            this.startupCounter = startupCounter;
            return this;
        }

        public Builder shutdownCounter(int shutdownCounter) {
            this.shutdownCounter = shutdownCounter;
            return this;
        }

        public Builder lastRfPerTick(int lastRfPerTick) {
            this.lastRfPerTick = lastRfPerTick;
            return this;
        }

        public GeneratorBlob build() {
            return new GeneratorBlob(this);
        }
    }
}
