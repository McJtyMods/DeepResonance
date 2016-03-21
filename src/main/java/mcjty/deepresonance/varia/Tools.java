package mcjty.deepresonance.varia;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Tools {
    // Server side: play a sound to all nearby players
    public static void playSound(World worldObj, String soundName, double x, double y, double z, double volume, double pitch) {
        S29PacketSoundEffect soundEffect = new S29PacketSoundEffect(soundName, x, y, z, (float) volume, (float) pitch);

        for (int j = 0; j < worldObj.playerEntities.size(); ++j) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)worldObj.playerEntities.get(j);
            BlockPos chunkcoordinates = entityplayermp.getPosition();
            double d7 = x - chunkcoordinates.getX();
            double d8 = y - chunkcoordinates.getY();
            double d9 = z - chunkcoordinates.getZ();
            double d10 = d7 * d7 + d8 * d8 + d9 * d9;

            if (d10 <= 256.0D) {
                entityplayermp.playerNetServerHandler.sendPacket(soundEffect);
            }
        }
    }
}
