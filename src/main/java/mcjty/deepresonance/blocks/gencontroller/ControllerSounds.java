package mcjty.deepresonance.blocks.gencontroller;

import mcjty.lib.varia.GlobalCoordinate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ControllerSounds {

    private static final Map<GlobalCoordinate, MovingSound> sounds = new HashMap<>();

    public static void stopSound(World worldObj, BlockPos pos) {
        GlobalCoordinate g = new GlobalCoordinate(pos, worldObj.provider.getDimensionId());
        if (sounds.containsKey(g)) {
            MovingSound movingSound = sounds.get(g);
            Minecraft.getMinecraft().getSoundHandler().stopSound(movingSound);
            sounds.remove(g);
        }
    }

    private static void playSound(World worldObj, BlockPos pos, MovingSound sound) {
        stopSound(worldObj, pos);
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        GlobalCoordinate g = new GlobalCoordinate(pos, worldObj.provider.getDimensionId());
        sounds.put(g, sound);
    }


    public static void playStartup(World worldObj, BlockPos pos) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        MovingSound sound = new GeneratorStartupSound(player, worldObj, pos.getX(), pos.getY(), pos.getZ());
        playSound(worldObj, pos, sound);
    }

    public static void playLoop(World worldObj, BlockPos pos) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        MovingSound sound = new GeneratorLoopSound(player, worldObj, pos.getX(), pos.getY(), pos.getZ());
        playSound(worldObj, pos, sound);
    }

    public static void playShutdown(World worldObj, BlockPos pos) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        MovingSound sound = new GeneratorShutdownSound(player, worldObj, pos.getX(), pos.getY(), pos.getZ());
        playSound(worldObj, pos, sound);
    }

    public static boolean isStartupPlaying(World worldObj, BlockPos pos) {
        GlobalCoordinate g = new GlobalCoordinate(pos, worldObj.provider.getDimensionId());
        MovingSound movingSound = sounds.get(g);
        return movingSound instanceof GeneratorStartupSound;
    }

    public static boolean isLoopPlaying(World worldObj, BlockPos pos) {
        GlobalCoordinate g = new GlobalCoordinate(pos, worldObj.provider.getDimensionId());
        MovingSound movingSound = sounds.get(g);
        return movingSound instanceof GeneratorLoopSound;
    }

    public static boolean isShutdownPlaying(World worldObj, BlockPos pos) {
        GlobalCoordinate g = new GlobalCoordinate(pos, worldObj.provider.getDimensionId());
        MovingSound movingSound = sounds.get(g);
        return movingSound instanceof GeneratorShutdownSound;
    }
}
