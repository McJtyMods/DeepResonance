package mcjty.deepresonance;

import mcjty.lib.preferences.PlayerPreferencesProperties;
import mcjty.lib.preferences.PreferencesDispatcher;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FMLEventHandlers {

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.player.worldObj.isRemote) {
            PreferencesProperties preferencesProperties = PlayerPreferencesProperties.getProperties(event.player);
            preferencesProperties.tick((EntityPlayerMP) event.player, DeepResonance.networkHandler.getNetworkWrapper());
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent.Entity event){
        // @todo move to a mcjtylib helper
        if (event.getEntity() instanceof EntityPlayer) {
            if (!event.getEntity().hasCapability(PlayerPreferencesProperties.PREFERENCES_CAPABILITY, null)) {
                event.addCapability(new ResourceLocation("McJtyLib", "Preferences"), new PreferencesDispatcher());
            }
        }
    }
}
