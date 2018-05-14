package mcjty.deepresonance.generatornetwork;

import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

    @Override
    public void clear() {
        networks.clear();
        lastId = 0;
    }

    public static DRGeneratorNetwork getChannels(World world) {
        return getData(world, DRGeneratorNetwork.class, GENERATOR_NETWORK_NAME);
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
    public void readFromNBT(NBTTagCompound tagCompound) {
        networks.clear();
        NBTTagList lst = tagCompound.getTagList("networks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < lst.tagCount() ; i++) {
            NBTTagCompound tc = lst.getCompoundTagAt(i);
            int channel = tc.getInteger("channel");
            Network value = new Network();
            value.readFromNBT(tc);
            networks.put(channel, value);
        }
        lastId = tagCompound.getInteger("lastId");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        NBTTagList lst = new NBTTagList();
        for (Map.Entry<Integer, Network> entry : networks.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setInteger("channel", entry.getKey());
            entry.getValue().writeToNBT(tc);
            lst.appendTag(tc);
        }
        tagCompound.setTag("networks", lst);
        tagCompound.setInteger("lastId", lastId);
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

        public NBTTagCompound writeToNBT(NBTTagCompound tagCompound){
            tagCompound.setInteger("refcount", generatorBlocks);
            tagCompound.setInteger("collectors", collectorBlocks);
            tagCompound.setInteger("energy", energy);
            tagCompound.setBoolean("active", active);
            tagCompound.setInteger("startup", startupCounter);
            tagCompound.setInteger("shutdown", shutdownCounter);
            return tagCompound;
        }

        public void readFromNBT(NBTTagCompound tagCompound){
            this.generatorBlocks = tagCompound.getInteger("refcount");
            this.collectorBlocks = tagCompound.getInteger("collectors");
            this.energy = tagCompound.getInteger("energy");
            this.active = tagCompound.getBoolean("active");
            this.startupCounter = tagCompound.getInteger("startup");
            this.shutdownCounter = tagCompound.getInteger("shutdown");
        }
    }
}
