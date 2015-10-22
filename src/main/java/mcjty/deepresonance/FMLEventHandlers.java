package mcjty.deepresonance;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mcjty.lib.preferences.PlayerPreferencesProperties;
import net.minecraftforge.common.IExtendedEntityProperties;

public class FMLEventHandlers {

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.player.worldObj.isRemote) {
            IExtendedEntityProperties properties = event.player.getExtendedProperties(PlayerPreferencesProperties.ID);
            if (properties instanceof PlayerPreferencesProperties) {
                PlayerPreferencesProperties preferencesProperties = (PlayerPreferencesProperties) properties;
                preferencesProperties.tick(DeepResonance.networkHandler.getNetworkWrapper());
            }
        }
    }


}
