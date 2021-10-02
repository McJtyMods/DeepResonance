package mcjty.deepresonance.modules.generator.data;

import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class DRGeneratorNetwork extends AbstractWorldData<DRGeneratorNetwork> {

    private static final String GENERATOR_NETWORK_NAME = "DRGeneratorNetwork";

    private int lastId = 0;

    private final Map<Integer,Network> networks = new HashMap<>();

    public DRGeneratorNetwork(String name) {
        super(name);
    }

    public void clear() {
        networks.clear();
        lastId = 0;
    }

    public static DRGeneratorNetwork getChannels(World world) {
        return getData(world, () -> new DRGeneratorNetwork(GENERATOR_NETWORK_NAME), GENERATOR_NETWORK_NAME);
    }

    public Network getOrCreateNetwork(int id) {
        Network channel = networks.get(id);
        if (channel == null) {
            channel = new Network();
            networks.put(id, channel);
        }
        return channel;
    }

    public Network getChannel(int id) {
        return networks.get(id);
    }

    public void deleteChannel(int id) {
        networks.remove(id);
    }

    public int newChannel() {
        lastId++;
        return lastId;
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        networks.clear();
        ListNBT lst = tagCompound.getList("networks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < lst.size() ; i++) {
            CompoundNBT tc = lst.getCompound(i);
            int channel = tc.getInt("channel");
            Network value = new Network();
            value.readFromNBT(tc);
            networks.put(channel, value);
        }
        lastId = tagCompound.getInt("lastId");
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        ListNBT lst = new ListNBT();
        for (Map.Entry<Integer, Network> entry : networks.entrySet()) {
            CompoundNBT tc = new CompoundNBT();
            tc.putInt("channel", entry.getKey());
            entry.getValue().writeToNBT(tc);
            lst.add(tc);
        }
        tagCompound.put("networks", lst);
        tagCompound.putInt("lastId", lastId);
        return tagCompound;
    }

    public static class Network {
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

        public CompoundNBT writeToNBT(CompoundNBT tagCompound){
            tagCompound.putInt("refcount", generatorBlocks);
            tagCompound.putInt("collectors", collectorBlocks);
            tagCompound.putInt("energy", energy);
            tagCompound.putBoolean("active", active);
            tagCompound.putInt("startup", startupCounter);
            tagCompound.putInt("shutdown", shutdownCounter);
            return tagCompound;
        }

        public void readFromNBT(CompoundNBT tagCompound){
            this.generatorBlocks = tagCompound.getInt("refcount");
            this.collectorBlocks = tagCompound.getInt("collectors");
            this.energy = tagCompound.getInt("energy");
            this.active = tagCompound.getBoolean("active");
            this.startupCounter = tagCompound.getInt("startup");
            this.shutdownCounter = tagCompound.getInt("shutdown");
        }
    }
}
