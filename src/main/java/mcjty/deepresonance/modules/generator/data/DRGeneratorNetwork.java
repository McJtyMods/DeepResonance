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
            .loader(Network::load)
            .saver(Network::save)
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
        private final int generatorBlocks;
        private final int collectorBlocks;
        private final int energy;
        private final boolean active;
        private final int startupCounter;
        private final int shutdownCounter;
        private final int lastRfPerTick;

        private Network(Builder builder) {
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
            return Network.builder()
                    .generatorBlocks(tagCompound.getInt("refcount"))
                    .collectorBlocks(tagCompound.getInt("collectors"))
                    .energy(tagCompound.getInt("energy"))
                    .active(tagCompound.getBoolean("active"))
                    .startupCounter(tagCompound.getInt("startup"))
                    .shutdownCounter(tagCompound.getInt("shutdown"))
                    .build();
        }

        public static CompoundNBT save(CompoundNBT tagCompound, Network network) {
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
            private int collectorBlocks = 0;
            private int energy = 0;
            private boolean active;
            private int startupCounter;
            private int shutdownCounter;
            private int lastRfPerTick;

            public Builder network(Network network) {
                generatorBlocks = network.generatorBlocks;
                collectorBlocks = network.collectorBlocks;
                energy = network.energy;
                active = network.active;
                startupCounter = network.startupCounter;
                shutdownCounter = network.shutdownCounter;
                lastRfPerTick = network.lastRfPerTick;
                return this;
            }

            public Builder merge(Network network) {
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

            public Network build() {
                return new Network(this);
            }
        }
    }
}
