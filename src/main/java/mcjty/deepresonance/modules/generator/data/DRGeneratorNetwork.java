package mcjty.deepresonance.modules.generator.data;

import mcjty.lib.multiblock.IMultiblock;
import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DRGeneratorNetwork extends AbstractWorldData<DRGeneratorNetwork> {

    private static final String GENERATOR_NETWORK_NAME = "DRGeneratorNetwork";

    private final MultiblockDriver<Network> driver = MultiblockDriver.<Network>builder()
            .blockSupplier(Network::load)
            .dirtySetter(d -> setDirty())
            .fixer(new GeneratorFixer())
            .holderGetter(
                    (world, blockPos) -> {
                        TileEntity be = world.getBlockEntity(blockPos);
                        if (be instanceof IMultiblockConnector) {
                            return (IMultiblockConnector) be;
                        } else {
                            return null;
                        }
                    })
            .build();

    public DRGeneratorNetwork(String name) {
        super(name);
    }

    public void clear() {
        driver.clear();
    }

    public MultiblockDriver<Network> getDriver() {
        return driver;
    }

    public static DRGeneratorNetwork getChannels(World world) {
        return getData(world, () -> new DRGeneratorNetwork(GENERATOR_NETWORK_NAME), GENERATOR_NETWORK_NAME);
    }

    public Network getNetwork(int id) {
        return driver.get(id);
    }

    public Network getChannel(int id) {
        return driver.get(id);
    }

    public void deleteChannel(int id) {
        driver.delete(id);
    }

    public int newChannel() {
        return driver.createId();
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
        private int generatorBlocks;
        private int collectorBlocks;
        private int energy;
        private boolean active;
        private int startupCounter;
        private int shutdownCounter;
        private int lastRfPerTick;

        // @TODO needs to be final/immutable!

        public Network(int generatorBlocks, int collectorBlocks, int energy, boolean active, int startupCounter, int shutdownCounter, int lastRfPerTick) {
            this.generatorBlocks = generatorBlocks;
            this.collectorBlocks = collectorBlocks;
            this.energy = energy;
            this.active = active;
            this.startupCounter = startupCounter;
            this.shutdownCounter = shutdownCounter;
            this.lastRfPerTick = lastRfPerTick;
        }

        public int getGeneratorBlocks() {
            return generatorBlocks;
        }

        public int getCollectorBlocks() {
            return collectorBlocks;
        }

        public int getEnergy() {
            return energy;
        }

        public int getLastRfPerTick() {
            return lastRfPerTick;
        }

        public boolean isActive() {
            return active;
        }

        public int getStartupCounter() {
            return startupCounter;
        }

        public int getShutdownCounter() {
            return shutdownCounter;
        }

        public static Network load(CompoundNBT tagCompound) {
            int generatorBlocks = tagCompound.getInt("refcount");
            int collectorBlocks = tagCompound.getInt("collectors");
            int energy = tagCompound.getInt("energy");
            boolean active = tagCompound.getBoolean("active");
            int startupCounter = tagCompound.getInt("startup");
            int shutdownCounter = tagCompound.getInt("shutdown");
            return new Network(generatorBlocks, collectorBlocks, energy, active, startupCounter, shutdownCounter, 0);
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
