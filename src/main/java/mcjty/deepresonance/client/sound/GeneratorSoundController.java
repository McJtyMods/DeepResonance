package mcjty.deepresonance.client.sound;

import com.google.common.collect.Maps;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.client.DRResourceLocation;
import mcjty.lib.varia.GlobalCoordinate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@SideOnly(Side.CLIENT)
public final class GeneratorSoundController {

    public static void init() {
        startup = registerSound(new DRResourceLocation("engine_start"));
        loop = registerSound(new DRResourceLocation("engine_loop"));
        shutDown = registerSound(new DRResourceLocation("engine_shutdown"));
    }

    private static final Map<GlobalCoordinate, GeneratorSound> sounds = Maps.newHashMap();
    protected static SoundEvent startup, loop, shutDown;

    private static SoundEvent registerSound(ResourceLocation rl){
        SoundEvent ret = new SoundEvent(rl).setRegistryName(rl);
        ForgeRegistries.SOUND_EVENTS.register(ret);
        return ret;
    }

    public static void stopSound(World worldObj, BlockPos pos) {
        GlobalCoordinate g = fromPosition(worldObj, pos);
        if (sounds.containsKey(g)) {
            MovingSound movingSound = sounds.get(g);
            Minecraft.getMinecraft().getSoundHandler().stopSound(movingSound);
            sounds.remove(g);
        }
    }

    private static void playSound(World worldObj, BlockPos pos, SoundEvent soundType) {
        GeneratorSound sound = new GeneratorSound(soundType, worldObj, pos);
        stopSound(worldObj, pos);
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        GlobalCoordinate g = new GlobalCoordinate(pos, worldObj.provider.getDimension());
        sounds.put(g, sound);
    }


    public static void playStartup(World worldObj, BlockPos pos) {
        playSound(worldObj, pos, startup);
    }

    public static void playLoop(World worldObj, BlockPos pos) {
        playSound(worldObj, pos, loop);
    }

    public static void playShutdown(World worldObj, BlockPos pos) {
        playSound(worldObj, pos, shutDown);
    }

    public static boolean isStartupPlaying(World worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(startup, worldObj, pos);
    }

    public static boolean isLoopPlaying(World worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(loop, worldObj, pos);
    }

    public static boolean isShutdownPlaying(World worldObj, BlockPos pos) {
        return isSoundTypePlayingAt(shutDown, worldObj, pos);
    }

    private static boolean isSoundTypePlayingAt(SoundEvent event, World world, BlockPos pos){
        GeneratorSound s = getSoundAt(world, pos);
        return s != null && s.isSoundType(event);
    }

    private static GeneratorSound getSoundAt(World world, BlockPos pos){
        return sounds.get(fromPosition(world, pos));
    }

    private static GlobalCoordinate fromPosition(World world, BlockPos pos){
        return new GlobalCoordinate(pos, WorldHelper.getDimID(world));
    }

}
