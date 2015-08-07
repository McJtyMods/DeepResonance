package mcjty.deepresonance.generatornetwork;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class DRGeneratorNetwork extends WorldSavedData {

    public static final String GENERATOR_NETWORK_NAME = "DRGeneratorNetwork";
    private static DRGeneratorNetwork instance = null;

    private int lastId = 0;

    private final Map<Integer,Network> networks = new HashMap<Integer,Network>();

    public DRGeneratorNetwork(String identifier) {
        super(identifier);
    }

    public void save(World world) {
        world.mapStorage.setData(GENERATOR_NETWORK_NAME, this);
        markDirty();
    }

    public static void clearInstance() {
        if (instance != null) {
            instance.networks.clear();
            instance = null;
        }
    }

    public static DRGeneratorNetwork getChannels() {
        return instance;
    }

    public static DRGeneratorNetwork getChannels(World world) {
        if (world.isRemote) {
            return null;
        }
        if (instance != null) {
            return instance;
        }
        instance = (DRGeneratorNetwork) world.mapStorage.loadData(DRGeneratorNetwork.class, GENERATOR_NETWORK_NAME);
        if (instance == null) {
            instance = new DRGeneratorNetwork(GENERATOR_NETWORK_NAME);
        }
        return instance;
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
    public void writeToNBT(NBTTagCompound tagCompound) {
        NBTTagList lst = new NBTTagList();
        for (Map.Entry<Integer, Network> entry : networks.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setInteger("channel", entry.getKey());
            entry.getValue().writeToNBT(tc);
            lst.appendTag(tc);
        }
        tagCompound.setTag("networks", lst);
        tagCompound.setInteger("lastId", lastId);
    }

    public static class Network {
        private int refcount = 0;
        private int energy = 0;

        public int getRefcount() {
            return refcount;
        }

        public void setRefcount(int refcount) {
            this.refcount = refcount;
        }

        public void incRefCount() {
            this.refcount++;
        }

        public void decRefCount() {
            this.refcount--;
        }

        public int getEnergy() {
            return energy;
        }

        public void setEnergy(int energy) {
            this.energy = energy;
        }

        public void writeToNBT(NBTTagCompound tagCompound){
            tagCompound.setInteger("refcount", refcount);
            tagCompound.setInteger("energy", energy);
        }

        public void readFromNBT(NBTTagCompound tagCompound){
            this.refcount = tagCompound.getInteger("refcount");
            this.energy = tagCompound.getInteger("energy");
        }
    }
}
