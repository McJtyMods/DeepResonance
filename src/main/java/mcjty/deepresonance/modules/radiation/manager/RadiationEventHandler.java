package mcjty.deepresonance.modules.radiation.manager;

import elec332.core.util.RegistryHelper;
import mcjty.deepresonance.api.radiation.IWorldRadiationManager;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;

/**
 * Created by Elec332 on 13-7-2020
 */
public class RadiationEventHandler {

    public static void register() {
        MinecraftForge.EVENT_BUS.addGenericListener(World.class, RadiationEventHandler::registerWorldCapabilities);
        MinecraftForge.EVENT_BUS.addListener(RadiationEventHandler::onTick);
    }

    private static void registerWorldCapabilities(AttachCapabilitiesEvent<World> worldCapabilitiesEvent) {
        if (worldCapabilitiesEvent.getObject().isRemote) {
            return;
        }
        final IWorldRadiationManager manager = new RadiationManager();
        RegistryHelper.registerCapability(worldCapabilitiesEvent, RadiationModule.CAPABILITY_NAME, RadiationModule.CAPABILITY, manager);
    }

    private static void onTick(TickEvent.WorldTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) {
            return;
        }
        World world = evt.world;
        LazyOptional<IWorldRadiationManager> cap = world.getCapability(RadiationModule.CAPABILITY);
        if (cap.isPresent()) {
            IWorldRadiationManager manager = cap.orElseThrow(NullPointerException::new);
            if (manager instanceof RadiationManager) {
                ((RadiationManager) manager).tick(world);
            }
        }
    }

}
