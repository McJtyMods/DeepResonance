package mcjty.deepresonance.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.items.RadiationMonitorItem;

@SideOnly(Side.CLIENT)
public class ReturnRadiationHelper {
    public static void setRadiationLevel(PacketReturnRadiation message) {
        RadiationMonitorItem.radiationStrength = message.getStrength();
    }
}
