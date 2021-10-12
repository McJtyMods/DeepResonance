package mcjty.deepresonance.modules.radiation.manager;

import com.google.common.collect.Maps;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.deepresonance.modules.radiation.util.RadiationShieldRegistry;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Map;

public class DRRadiationManager extends AbstractWorldData<DRRadiationManager> {

    private static final String RADIATION_MANAGER_NAME = "DRRadiationManager";

    private final Map<GlobalCoordinate, RadiationSource> sources = Maps.newHashMap();

    public DRRadiationManager(String name) {
        super(name);
    }



    public void clear() {
        sources.clear();
    }

    public static float calculateRadiationStrength(float strength, float purity) {
        float p = (float) Math.log10(purity / 100.0f) + 1.0f;
        if (p < 0.01f) {
            p = 0.01f;
        }
        double str = RadiationConfiguration.MIN_RADIATION_STRENGTH.get() + strength * (1.0f - p) / 100.0f
                * (RadiationConfiguration.MAX_RADIATION_STRENGTH.get() - RadiationConfiguration.MIN_RADIATION_STRENGTH.get());
        return (float) str;
    }

    public static float calculateRadiationRadius(float strength, float efficiency, float purity) {
        double radius = RadiationConfiguration.MIN_RADIATION_RADIUS.get() + (strength + efficiency) / 200.0f
                * (RadiationConfiguration.MAX_RADIATION_RADIUS.get() - RadiationConfiguration.MIN_RADIATION_RADIUS.get());
        radius += radius * (100.0f - purity) * .002f;
        return (float) radius;
    }

    public void removeAllRadiation() {
        sources.clear();
    }

    public static DRRadiationManager getManager(World world) {
        return getData(world, () -> new DRRadiationManager(RADIATION_MANAGER_NAME), RADIATION_MANAGER_NAME);
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
    public void load(CompoundNBT tagCompound) {
        sources.clear();
        ListNBT lst = tagCompound.getList("radiation", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < lst.size(); i++) {
            CompoundNBT tc = lst.getCompound(i);
            ResourceLocation id = new ResourceLocation(tc.getString("dim"));
            DimensionId type = DimensionId.fromResourceLocation(id);
            GlobalCoordinate coordinate = new GlobalCoordinate(new BlockPos(tc.getInt("sourceX"), tc.getInt("sourceY"), tc.getInt("sourceZ")), type);
            RadiationSource value = new RadiationSource();
            value.readFromNBT(tc);
            sources.put(coordinate, value);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        ListNBT lst = new ListNBT();
        for (Map.Entry<GlobalCoordinate, RadiationSource> entry : sources.entrySet()) {
            CompoundNBT tc = new CompoundNBT();
            tc.putString("dim", entry.getKey().getDimension().getRegistryName().toString());
            tc.putInt("sourceX", entry.getKey().getCoordinate().getX());
            tc.putInt("sourceY", entry.getKey().getCoordinate().getY());
            tc.putInt("sourceZ", entry.getKey().getCoordinate().getZ());
            entry.getValue().writeToNBT(tc);
            lst.add(tc);
        }
        tagCompound.put("radiation", lst);
        return tagCompound;
    }

    public static class RadiationSource {
        private float radius;
        private float maxStrength;              // Roughly an indication of the amount of ticks.
        private float strength;
        private QuadTree radiationTree;


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

        public QuadTree getRadiationTree(World world, int centerX, int centerY, int centerZ) {
            if (radiationTree == null) {
                radiationTree = new QuadTree((int) (centerX-radius - 1), (int) (centerY-radius - 1), (int) (centerZ-radius-1), (int) (centerX+radius + 1), (int) (centerY+radius + 1), (int) (centerZ+radius + 1));
                BlockPos.Mutable pos = new BlockPos.Mutable();
                for (int x = (int) (centerX-radius); x < centerX+radius ; x++) {
                    for (int y = (int) (centerY-radius); y < centerY+radius ; y++) {
                        for (int z = (int) (centerZ-radius); z < centerZ+radius ; z++) {
                            pos.set(x, y, z);
                            BlockState block = world.getBlockState(pos);
                            float blocker = RadiationShieldRegistry.getBlocker(block);
                            if (blocker < 0.99f) {
                                radiationTree.addBlocker(pos, blocker);
                            }
                        }
                    }
                }
            }
            return radiationTree;
        }

        // Update radiation for this radiation source
        // @param ticks is the amount of ticks to update for.
        public void update(float radius, float maxStrenght, int ticks) {
            this.maxStrength = maxStrenght;
            this.radius = radius;
            double toadd = maxStrenght * RadiationConfiguration.STRENGTH_GROWTH_FACTOR.get() * ticks;
            if ((strength + toadd) > maxStrenght) {
                toadd = maxStrenght - strength;
                if (toadd < 0) {
                    toadd = 0;
                }
            }
            strength += toadd;
        }

        public void writeToNBT(CompoundNBT tagCompound) {
            tagCompound.putFloat("radius", radius);
            tagCompound.putFloat("maxStrength", maxStrength);
            tagCompound.putFloat("strength", strength);
        }

        public void readFromNBT(CompoundNBT tagCompound) {
            this.radius = tagCompound.getFloat("radius");
            this.maxStrength = tagCompound.getFloat("maxStrength");
            this.strength = tagCompound.getFloat("strength");
        }
    }
}