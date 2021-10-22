package mcjty.deepresonance.modules.generator.data;

import mcjty.lib.multiblock.IMultiblock;
import net.minecraft.nbt.CompoundNBT;

public class GeneratorNetwork implements IMultiblock {
    private final int generatorBlocks;
    private int collectorBlocks;
    private int energy;
    private boolean active;
    private int startupCounter;
    private int shutdownCounter;
    private int lastRfPerTick;

    private GeneratorNetwork(Builder builder) {
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

    public static GeneratorNetwork load(CompoundNBT tagCompound) {
        return GeneratorNetwork.builder()
                .generatorBlocks(tagCompound.getInt("refcount"))
                .collectorBlocks(tagCompound.getInt("collectors"))
                .energy(tagCompound.getInt("energy"))
                .active(tagCompound.getBoolean("active"))
                .startupCounter(tagCompound.getInt("startup"))
                .shutdownCounter(tagCompound.getInt("shutdown"))
                .build();
    }

    public static CompoundNBT save(CompoundNBT tagCompound, GeneratorNetwork network) {
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

        public Builder network(GeneratorNetwork network) {
            generatorBlocks = network.generatorBlocks;
            collectorBlocks = network.collectorBlocks;
            energy = network.energy;
            active = network.active;
            startupCounter = network.startupCounter;
            shutdownCounter = network.shutdownCounter;
            lastRfPerTick = network.lastRfPerTick;
            return this;
        }

        public Builder merge(GeneratorNetwork network) {
            generatorBlocks += network.generatorBlocks;
            collectorBlocks += network.collectorBlocks;
            energy += network.energy;
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

        public GeneratorNetwork build() {
            return new GeneratorNetwork(this);
        }
    }
}
