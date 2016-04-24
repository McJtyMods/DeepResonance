package mcjty.deepresonance;

import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.RadiationShieldRegistry;
import mcjty.deepresonance.varia.QuadTree;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        float blocker = RadiationShieldRegistry.getBlocker(event.getState());
        if (blocker >= 0.99f) {
            return;
        }

        World world = event.getWorld();
        DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
        Map<GlobalCoordinate, DRRadiationManager.RadiationSource> radiationSources = radiationManager.getRadiationSources();
        if (radiationSources.isEmpty()) {
            return;
        }

        int x = event.getPos().getX();
        int y = event.getPos().getY();
        int z = event.getPos().getZ();

        for (Map.Entry<GlobalCoordinate, DRRadiationManager.RadiationSource> entry : radiationSources.entrySet()) {
            DRRadiationManager.RadiationSource source = entry.getValue();
            float radius = source.getRadius();
            GlobalCoordinate gc = entry.getKey();
            BlockPos c = gc.getCoordinate();
            if (Math.abs(c.getX()-x) < radius && Math.abs(c.getY()-y) < radius && Math.abs(c.getZ()-z) < radius) {
                Logging.logDebug("Removed blocker at: " + x + "," + y + "," + z);
                QuadTree radiationTree = source.getRadiationTree(world, c.getX(), c.getY(), c.getZ());
                radiationTree.addBlocker(x, y, z, 1.0f);
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaceEvent(BlockEvent.PlaceEvent event) {
        float blocker = RadiationShieldRegistry.getBlocker(event.getState());
        if (blocker >= 0.99f) {
            return;
        }

        World world = event.getBlockSnapshot().getWorld();
        DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
        Map<GlobalCoordinate, DRRadiationManager.RadiationSource> radiationSources = radiationManager.getRadiationSources();
        if (radiationSources.isEmpty()) {
            return;
        }

        int x = event.getBlockSnapshot().getPos().getX();
        int y = event.getBlockSnapshot().getPos().getY();
        int z = event.getBlockSnapshot().getPos().getZ();
        for (Map.Entry<GlobalCoordinate, DRRadiationManager.RadiationSource> entry : radiationSources.entrySet()) {
            DRRadiationManager.RadiationSource source = entry.getValue();
            float radius = source.getRadius();
            GlobalCoordinate gc = entry.getKey();
            BlockPos c = gc.getCoordinate();
            if (Math.abs(c.getX()-x) < radius && Math.abs(c.getY()-y) < radius && Math.abs(c.getZ()-z) < radius) {
                Logging.logDebug("Add blocker at: " + x + "," + y + "," + z);
                QuadTree radiationTree = source.getRadiationTree(world, c.getX(), c.getY(), c.getZ());
                radiationTree.addBlocker(x, y, z, blocker);
            }
        }

    }
}
