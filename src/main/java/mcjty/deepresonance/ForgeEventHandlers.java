package mcjty.deepresonance;

import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.blocks.sensors.AbstractSensorTileEntity;
import mcjty.deepresonance.network.DRMessages;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.RadiationShieldRegistry;
import mcjty.deepresonance.varia.QuadTree;
import mcjty.lib.network.PacketFinalizeLogin;
import mcjty.lib.network.PacketHandler;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Map;

public class ForgeEventHandlers {

    @SubscribeEvent
    public static void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equals(DeepResonance.MODID)) {
            ConfigManager.sync(DeepResonance.MODID, Config.Type.INSTANCE);
        }
    }

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
        if (world.isRemote) {
            // Can normally not happen but in rare situations the PlaceEvent can get called client-side
            return;
        }

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

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        DRMessages.INSTANCE.sendTo(new PacketFinalizeLogin(), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        PacketHandler.onDisconnect();
    }

    @SubscribeEvent
    public void onPostWorldTick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote) {
            for (ResonatingCrystalTileEntity crystal : ResonatingCrystalTileEntity.todoCrystals) {
                crystal.realUpdate();
            }
            ResonatingCrystalTileEntity.todoCrystals.clear();
            for (AbstractSensorTileEntity sensor : AbstractSensorTileEntity.todoSensors) {
                sensor.realUpdate();
            }
            AbstractSensorTileEntity.todoSensors.clear();
        }
    }


}
