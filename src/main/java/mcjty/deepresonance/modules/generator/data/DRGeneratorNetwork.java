package mcjty.deepresonance.modules.generator.data;

import mcjty.lib.multiblock.IMultiblock;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class DRGeneratorNetwork extends AbstractWorldData<DRGeneratorNetwork> {

    private static final String GENERATOR_NETWORK_NAME = "DRGeneratorNetwork";

    private final MultiblockDriver<Network> driver = new MultiblockDriver<Network>(Network::new, d -> setDirty());

    public DRGeneratorNetwork(String name) {
        super(name);
    }

    public void clear() {
        driver.clear();
    }

    public static DRGeneratorNetwork getChannels(World world) {
        return getData(world, () -> new DRGeneratorNetwork(GENERATOR_NETWORK_NAME), GENERATOR_NETWORK_NAME);
    }

    public Network getOrCreateNetwork(int id) {
        return driver.getOrCreate(id);
    }

    public Network getChannel(int id) {
        return driver.get(id);
    }

    public void deleteChannel(int id) {
        driver.delete(id);
    }

    public int newChannel() {
        return driver.create();
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        driver.load(tagCompound);
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        return driver.save(tagCompound);
    }

    public static class Network implements IMultiblock {
        private int generatorBlocks = 0;
        private int collectorBlocks = 0;
        private int energy = 0;
        private boolean active = false;
        private int startupCounter = 0;
        private int shutdownCounter = 0;
        private int lastRfPerTick = 0;

        public int getGeneratorBlocks() {
            return generatorBlocks;
        }

        public void setGeneratorBlocks(int generatorBlocks) {
            this.generatorBlocks = generatorBlocks;
        }

        public void incGeneratorBlocks() {
            this.generatorBlocks++;
        }

        public void decGeneratorBlocks() {
            this.generatorBlocks--;
        }

        public int getCollectorBlocks() {
            return collectorBlocks;
        }

        public void setCollectorBlocks(int collectorBlocks) {
            this.collectorBlocks = collectorBlocks;
        }

        public void incCollectorBlocks() {
            collectorBlocks++;
        }

        public void decCollectorBlocks() {
            collectorBlocks--;
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

        @Override
        public void load(CompoundNBT tagCompound) {
            this.generatorBlocks = tagCompound.getInt("refcount");
            this.collectorBlocks = tagCompound.getInt("collectors");
            this.energy = tagCompound.getInt("energy");
            this.active = tagCompound.getBoolean("active");
            this.startupCounter = tagCompound.getInt("startup");
            this.shutdownCounter = tagCompound.getInt("shutdown");
        }

        @Override
        public CompoundNBT save(CompoundNBT tagCompound) {
            tagCompound.putInt("refcount", generatorBlocks);
            tagCompound.putInt("collectors", collectorBlocks);
            tagCompound.putInt("energy", energy);
            tagCompound.putBoolean("active", active);
            tagCompound.putInt("startup", startupCounter);
            tagCompound.putInt("shutdown", shutdownCounter);
            return tagCompound;
        }
    }
}
