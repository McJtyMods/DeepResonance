package mcjty.deepresonance.modules.generator.sound;

import com.google.common.collect.Maps;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.Map;


public final class GeneratorSoundController {

    private static final Map<GlobalPos, GeneratorSound> sounds = Maps.newHashMap();

    public static void stopSound(World worldObj, BlockPos pos) {
        GlobalPos g = GlobalPos.of(worldObj.dimension(), pos);
        if (sounds.containsKey(g)) {
            TickableSound movingSound = sounds.get(g);
            Minecraft.getInstance().getSoundManager().stop(movingSound);
            sounds.remove(g);
        }
    }

    private static void playSound(World worldObj, BlockPos pos, SoundEvent soundType) {
        GeneratorSound sound = new GeneratorSound(soundType, worldObj, pos);
        stopSound(worldObj, pos);
        Minecraft.getInstance().getSoundManager().play(sound);
        GlobalPos g = GlobalPos.of(worldObj.dimension(), pos);
        sounds.put(g, sound);
    }


    public static void playStartup(World worldObj, BlockPos pos) {
        playSound(worldObj, pos, GeneratorModule.STARTUP_SOUND.get());
    }

    public static void playLoop(World worldObj, BlockPos pos) {
        playSound(worldObj, pos, GeneratorModule.LOOP_SOUND.get());
    }

    public static void playShutdown(World worldObj, BlockPos pos) {
        playSound(worldObj, pos, GeneratorModule.SHUTDOWN_SOUND.get());
    }

    public static boolean isStartupPlaying(World worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(GeneratorModule.STARTUP_SOUND.get(), worldObj, pos);
    }

    public static boolean isLoopPlaying(World worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(GeneratorModule.LOOP_SOUND.get(), worldObj, pos);
    }

    public static boolean isShutdownPlaying(World worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(GeneratorModule.SHUTDOWN_SOUND.get(), worldObj, pos);
    }

    private static boolean isSoundTypePlayingAt(SoundEvent event, World world, BlockPos pos){
        GeneratorSound s = getSoundAt(world, pos);
        return s != null && s.isSoundType(event);
    }

    private static GeneratorSound getSoundAt(World world, BlockPos pos){
        return sounds.get(GlobalPos.of(world.dimension(), pos));
    }

}
