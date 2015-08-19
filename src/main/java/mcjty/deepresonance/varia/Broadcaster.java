package mcjty.deepresonance.varia;

import mcjty.varia.Logging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class Broadcaster {
    public static void broadcast(World worldObj, int x, int y, int z, String message, float radius) {
        for (Object p : worldObj.playerEntities) {
            EntityPlayer player = (EntityPlayer) p;
            double sqdist = player.getDistanceSq(x + .5, y + .5, z + .5);
            if (sqdist < radius) {
                Logging.warn(player, message);
            }
        }
    }
}
