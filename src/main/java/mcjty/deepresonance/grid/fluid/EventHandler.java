package mcjty.deepresonance.grid.fluid;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.grid.fluid.event.FluidTileEvent;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class EventHandler {

    @SubscribeEvent
    public void onEnergyTileAdded(FluidTileEvent.Load event){
        DeepResonance.worldGridRegistry.get(event.world).addTile(event.tile);
    }

    @SubscribeEvent
    public void onEnergyTileRemoved(FluidTileEvent.Unload event){
        DeepResonance.worldGridRegistry.get(event.world).removeTile(event.tile);
    }

}
