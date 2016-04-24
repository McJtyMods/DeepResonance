package mcjty.deepresonance.grid.fluid;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.grid.fluid.event.FluidTileEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class EventHandler {

    @SubscribeEvent
    public void onEnergyTileAdded(FluidTileEvent.Load event){
        DeepResonance.worldGridRegistry.getFluidRegistry().get(event.getWorld()).addTile(event.tile);
    }

    @SubscribeEvent
    public void onEnergyTileRemoved(FluidTileEvent.Unload event){
        DeepResonance.worldGridRegistry.getFluidRegistry().get(event.getWorld()).removeTile(event.tile);
    }

}
