package mcjty.deepresonance.network;

import mcjty.deepresonance.items.RadiationMonitorItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReturnRadiationHelper {
    public static void setRadiationLevel(PacketReturnRadiation message) {
        RadiationMonitorItem.radiationStrength = message.getStrength();
    }
}
