package mcjty.deepresonance.radiation;

import mcjty.varia.Coordinate;
import mcjty.varia.GlobalCoordinate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class DRRadiationManager extends WorldSavedData {

    public static final String RADIATION_MANAGER_NAME = "DRRadiationManager";
    private static DRRadiationManager instance = null;

    private final Map<GlobalCoordinate, RadiationSource> sources = new HashMap<GlobalCoordinate, RadiationSource>();

    public DRRadiationManager(String identifier) {
        super(identifier);
    }

    public void save(World world) {
        world.mapStorage.setData(RADIATION_MANAGER_NAME, this);
        markDirty();
    }

    public static void clearInstance() {
        if (instance != null) {
            instance.sources.clear();
            instance = null;
        }
    }

    public static DRRadiationManager getManager() {
        return instance;
    }

    public static DRRadiationManager getManager(World world) {
        if (world.isRemote) {
            return null;
        }
        if (instance != null) {
            return instance;
        }
        instance = (DRRadiationManager) world.mapStorage.loadData(DRRadiationManager.class, RADIATION_MANAGER_NAME);
        if (instance == null) {
            instance = new DRRadiationManager(RADIATION_MANAGER_NAME);
        }
        return instance;
    }

    public RadiationSource getOrCreateRadiationSource(GlobalCoordinate coordinate) {
        RadiationSource source = sources.get(coordinate);
        if (source == null) {
            source = new RadiationSource();
            sources.put(coordinate, source);
        }
        return source;
    }

    public RadiationSource getRadiationSource(GlobalCoordinate coordinate) {
        return sources.get(coordinate);
    }

    public Map<GlobalCoordinate, RadiationSource> getRadiationSources() {
        return sources;
    }

    public void deleteRadiationSource(GlobalCoordinate coordinate) {
        sources.remove(coordinate);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        sources.clear();
        NBTTagList lst = tagCompound.getTagList("radiation", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < lst.tagCount(); i++) {
            NBTTagCompound tc = lst.getCompoundTagAt(i);
            GlobalCoordinate coordinate = new GlobalCoordinate(new Coordinate(tc.getInteger("sourceX"), tc.getInteger("sourceY"), tc.getInteger("sourceZ")), tc.getInteger("dimension"));
            RadiationSource value = new RadiationSource();
            value.readFromNBT(tc);
            sources.put(coordinate, value);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        NBTTagList lst = new NBTTagList();
        for (Map.Entry<GlobalCoordinate, RadiationSource> entry : sources.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setInteger("dimension", entry.getKey().getDimension());
            tc.setInteger("sourceX", entry.getKey().getCoordinate().getX());
            tc.setInteger("sourceY", entry.getKey().getCoordinate().getY());
            tc.setInteger("sourceZ", entry.getKey().getCoordinate().getZ());
            entry.getValue().writeToNBT(tc);
            lst.appendTag(tc);
        }
        tagCompound.setTag("radiation", lst);
    }

    public static class RadiationSource {
        private float radius;
        private float maxStrength;              // Roughly an indication of the amount of ticks.
        private float strength;

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public float getMaxStrength() {
            return maxStrength;
        }

        public void setMaxStrength(float maxStrength) {
            this.maxStrength = maxStrength;
        }

        public float getStrength() {
            return strength;
        }

        public void setStrength(float strength) {
            this.strength = strength;
        }

        // Update radiation for this radiation source
        public void update(float radius, float maxStrenght) {
            this.maxStrength = maxStrenght;
            this.radius = radius;
            if (strength < maxStrenght) {
                strength += 2.0;
            }
        }

        public void writeToNBT(NBTTagCompound tagCompound) {
            tagCompound.setFloat("radius", radius);
            tagCompound.setFloat("maxStrength", maxStrength);
            tagCompound.setFloat("strength", strength);
        }

        public void readFromNBT(NBTTagCompound tagCompound) {
            this.radius = tagCompound.getFloat("radius");
            this.maxStrength = tagCompound.getFloat("maxStrength");
            this.strength = tagCompound.getFloat("strength");
        }
    }
}