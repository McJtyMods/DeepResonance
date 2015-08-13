package mcjty.deepresonance.radiation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mcjty.varia.GlobalCoordinate;
import mcjty.varia.Logging;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RadiationTickEvent {
    public static final int MAXTICKS = 10;
    private int counter = MAXTICKS;

    private static final int EFFECTS_MAX = 18;
    private int counterEffects = EFFECTS_MAX;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent evt) {
        if (evt.phase == TickEvent.Phase.START) {
            return;
        }
        counter--;
        if (counter <= 0) {
            counter = MAXTICKS;

            counterEffects--;
            boolean doEffects = false;
            if (counterEffects <= 0) {
                counterEffects = EFFECTS_MAX;
                doEffects = true;
            }
            serverTick(doEffects);
        }
    }

    private void serverTick(boolean doEffects) {
        World entityWorld = MinecraftServer.getServer().getEntityWorld();
        DRRadiationManager radiationManager = DRRadiationManager.getManager(entityWorld);

        Set<GlobalCoordinate> toRemove = new HashSet<GlobalCoordinate>();
        boolean dirty = false;

        for (Map.Entry<GlobalCoordinate, DRRadiationManager.RadiationSource> source : radiationManager.getRadiationSources().entrySet()) {
            GlobalCoordinate coordinate = source.getKey();
            World world = DimensionManager.getWorld(coordinate.getDimension());
            if (world != null) {
                if (world.getChunkProvider().chunkExists(coordinate.getCoordinate().getX() >> 4, coordinate.getCoordinate().getZ() >> 4)) {
                    // The world is loaded and the chunk containing the radiation source is also loaded.
                    DRRadiationManager.RadiationSource radiationSource = source.getValue();
                    float strength = radiationSource.getStrength();

                    strength -= RadiationConfiguration.strengthDecreasePerTick * MAXTICKS;
                    dirty = true;
                    if (strength <= 0) {
                        toRemove.add(coordinate);
                    } else {
                        radiationSource.setStrength(strength);

                        if (doEffects) {
                            handleRadiationEffects(world, coordinate, radiationSource);
                        }

                    }
                }
            }
        }

        if (dirty) {
            for (GlobalCoordinate coordinate : toRemove) {
                radiationManager.deleteRadiationSource(coordinate);
                Logging.log("Removed radiation source at: " + coordinate.getCoordinate().toString() + " (" + coordinate.getDimension() + ")");
            }

            radiationManager.save(entityWorld);
        }
    }

    private void handleRadiationEffects(World world, GlobalCoordinate coordinate, DRRadiationManager.RadiationSource radiationSource) {
        double dx = coordinate.getCoordinate().getX();
        double dy = coordinate.getCoordinate().getY();
        double dz = coordinate.getCoordinate().getZ();
        double radius = radiationSource.getRadius();
        double radiusSq = radius * radius;
        float baseStrength = radiationSource.getStrength();

        List list = world.selectEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(dx - radius, dy - radius, dz - radius, dx + radius, dy + radius, dz + radius), null);
        for (Object o : list) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) o;

            double distanceSq = entityLivingBase.getDistanceSq(dx, dy, dz);
            if (distanceSq < radiusSq) {
                double distance = Math.sqrt(distanceSq);
                float strength = (float) (baseStrength * (radius-distance));

                if (strength < RadiationConfiguration.radiationStrenghLevel0) {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 1, true));
                } else if (strength < RadiationConfiguration.radiationStrenghLevel1) {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), EFFECTS_MAX * MAXTICKS, 1, true));
                } else if (strength < RadiationConfiguration.radiationStrenghLevel2) {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.weakness.getId(), EFFECTS_MAX * MAXTICKS, 1, true));
                } else if (strength < RadiationConfiguration.radiationStrenghLevel3) {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.weakness.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.poison.getId(), EFFECTS_MAX * MAXTICKS, 1, true));
                } else if (strength < RadiationConfiguration.radiationStrenghLevel4) {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.weakness.getId(), EFFECTS_MAX * MAXTICKS, 3, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.poison.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                } else {
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.hunger.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.weakness.getId(), EFFECTS_MAX * MAXTICKS, 3, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.poison.getId(), EFFECTS_MAX * MAXTICKS, 3, true));
                    entityLivingBase.addPotionEffect(new PotionEffect(Potion.wither.getId(), EFFECTS_MAX * MAXTICKS, 2, true));
                }
            }
        }

    }

}
