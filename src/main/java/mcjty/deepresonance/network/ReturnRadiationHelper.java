package mcjty.deepresonance.network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcjty.deepresonance.items.RadiationMonitorItem;

@SideOnly(Side.CLIENT)
public class ReturnRadiationHelper {
    public static void setRadiationLevel(PacketReturnRadiation message) {
        RadiationMonitorItem.radiationStrength = message.getStrength();
    }
}
