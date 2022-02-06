package mcjty.deepresonance.modules.generator.sound;

import com.google.common.collect.Maps;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;

import java.util.Map;


public final class GeneratorSoundController {

    private static final Map<GlobalPos, GeneratorSound> sounds = Maps.newHashMap();

    public static void stopSound(Level worldObj, BlockPos pos) {
        GlobalPos g = GlobalPos.of(worldObj.dimension(), pos);
        if (sounds.containsKey(g)) {
            AbstractTickableSoundInstance movingSound = sounds.get(g);
            Minecraft.getInstance().getSoundManager().stop(movingSound);
            sounds.remove(g);
        }
    }

    private static void playSound(Level worldObj, BlockPos pos, SoundEvent soundType) {
        GeneratorSound sound = new GeneratorSound(soundType, worldObj, pos);
        stopSound(worldObj, pos);
        Minecraft.getInstance().getSoundManager().play(sound);
        GlobalPos g = GlobalPos.of(worldObj.dimension(), pos);
        sounds.put(g, sound);
    }


    public static void playStartup(Level worldObj, BlockPos pos) {
        playSound(worldObj, pos, GeneratorModule.STARTUP_SOUND.get());
    }

    public static void playLoop(Level worldObj, BlockPos pos) {
        playSound(worldObj, pos, GeneratorModule.LOOP_SOUND.get());
    }

    public static void playShutdown(Level worldObj, BlockPos pos) {
        playSound(worldObj, pos, GeneratorModule.SHUTDOWN_SOUND.get());
    }

    public static boolean isStartupPlaying(Level worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(GeneratorModule.STARTUP_SOUND.get(), worldObj, pos);
    }

    public static boolean isLoopPlaying(Level worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(GeneratorModule.LOOP_SOUND.get(), worldObj, pos);
    }

    public static boolean isShutdownPlaying(Level worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(GeneratorModule.SHUTDOWN_SOUND.get(), worldObj, pos);
    }

    private static boolean isSoundTypePlayingAt(SoundEvent event, Level world, BlockPos pos){
        GeneratorSound s = getSoundAt(world, pos);
        return s != null && s.isSoundType(event);
    }

    private static GeneratorSound getSoundAt(Level world, BlockPos pos){
        return sounds.get(GlobalPos.of(world.dimension(), pos));
    }

}
