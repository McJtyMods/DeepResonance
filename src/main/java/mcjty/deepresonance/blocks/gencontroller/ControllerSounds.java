package mcjty.deepresonance.blocks.gencontroller;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.varia.GlobalCoordinate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ControllerSounds {

    private static final Map<GlobalCoordinate, MovingSound> sounds = new HashMap<>();

    public static final String[] REGISTER_SOUND = { "registerSound", "func_187502_a", "a" };

    public static void init() {
        try {
            Method m = ReflectionHelper.findMethod(SoundEvent.class, null, REGISTER_SOUND, String.class);
            m.invoke(null, DeepResonance.MODID + ":engine_start");
            m.invoke(null, DeepResonance.MODID + ":engine_loop");
            m.invoke(null, DeepResonance.MODID + ":engine_shutdown");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    public static void stopSound(World worldObj, BlockPos pos) {
        GlobalCoordinate g = new GlobalCoordinate(pos, worldObj.provider.getDimension());
        if (sounds.containsKey(g)) {
            MovingSound movingSound = sounds.get(g);
            Minecraft.getMinecraft().getSoundHandler().stopSound(movingSound);
            sounds.remove(g);
        }
    }

    private static void playSound(World worldObj, BlockPos pos, MovingSound sound) {
        stopSound(worldObj, pos);
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        GlobalCoordinate g = new GlobalCoordinate(pos, worldObj.provider.getDimension());
        sounds.put(g, sound);
    }


    public static void playStartup(World worldObj, BlockPos pos) {
        MovingSound sound = new GeneratorStartupSound(worldObj, pos.getX(), pos.getY(), pos.getZ());
        playSound(worldObj, pos, sound);
    }

    public static void playLoop(World worldObj, BlockPos pos) {
        MovingSound sound = new GeneratorLoopSound(worldObj, pos.getX(), pos.getY(), pos.getZ());
        playSound(worldObj, pos, sound);
    }

    public static void playShutdown(World worldObj, BlockPos pos) {
        MovingSound sound = new GeneratorShutdownSound(worldObj, pos.getX(), pos.getY(), pos.getZ());
        playSound(worldObj, pos, sound);
    }

    public static boolean isStartupPlaying(World worldObj, BlockPos pos) {
        GlobalCoordinate g = new GlobalCoordinate(pos, worldObj.provider.getDimension());
        MovingSound movingSound = sounds.get(g);
        return movingSound instanceof GeneratorStartupSound;
    }

    public static boolean isLoopPlaying(World worldObj, BlockPos pos) {
        GlobalCoordinate g = new GlobalCoordinate(pos, worldObj.provider.getDimension());
        MovingSound movingSound = sounds.get(g);
        return movingSound instanceof GeneratorLoopSound;
    }

    public static boolean isShutdownPlaying(World worldObj, BlockPos pos) {
        GlobalCoordinate g = new GlobalCoordinate(pos, worldObj.provider.getDimension());
        MovingSound movingSound = sounds.get(g);
        return movingSound instanceof GeneratorShutdownSound;
    }
}
