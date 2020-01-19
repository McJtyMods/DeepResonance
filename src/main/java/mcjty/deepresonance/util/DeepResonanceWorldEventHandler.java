package mcjty.deepresonance.util;

import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Created by Elec332 on 19-1-2020
 */
public class DeepResonanceWorldEventHandler {

    @SubscribeEvent
    public void onPostWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world.isRemote) {
            return;
        }
        DeepResonanceTickHandler.INSTANCE.tickType(TileEntityResonatingCrystal.class);
    }

}
