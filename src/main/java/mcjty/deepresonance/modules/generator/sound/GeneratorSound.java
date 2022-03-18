package mcjty.deepresonance.modules.generator.sound;

import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class GeneratorSound extends AbstractTickableSoundInstance {

    public GeneratorSound(SoundEvent event, Level world, BlockPos pos) {
        super(event, SoundSource.BLOCKS);
        this.world = world;
        this.pos = pos;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.delay = 0;
        this.loop = event == GeneratorModule.LOOP_SOUND.get();
        this.sound = event;
        this.relative = false;
    }

    private final Level world;
    private final BlockPos pos;
    private final boolean loop;
    private final SoundEvent sound;


    @Override
    public void tick() {
        Block block = world.getBlockState(pos).getBlock();
        if (block != GeneratorModule.GENERATOR_CONTROLLER_BLOCK.get()) {
            stop();
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        double distance = Math.sqrt(this.pos.distToCenterSqr(player.getX(), player.getY(), player.getZ()));
        if (distance > 20) {
            volume = 0;
        } else {
            volume = (float) (GeneratorConfig.BASE_GENERATOR_VOLUME.get() * (20-distance)/20.0);
        }
    }

    protected boolean isSoundType(SoundEvent event){
        return sound == event;
    }

}
