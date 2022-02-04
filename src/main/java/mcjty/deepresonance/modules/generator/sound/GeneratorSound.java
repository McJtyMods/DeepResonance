package mcjty.deepresonance.modules.generator.sound;

import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import net.minecraft.block.Block;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GeneratorSound extends TickableSound {

    public GeneratorSound(SoundEvent event, World world, BlockPos pos) {
        super(event, SoundCategory.BLOCKS);
        this.world = world;
        this.pos = pos;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.attenuation = AttenuationType.LINEAR;
        this.looping = true;
        this.delay = 0;
        this.loop = event == GeneratorModule.LOOP_SOUND.get();
        this.sound = event;
        this.relative = false;
    }

    private final World world;
    private final BlockPos pos;
    private final boolean loop;
    private final SoundEvent sound;
    private float scaleDown = 1.0f;


    @Override
    public void tick() {
        Block block = world.getBlockState(pos).getBlock();
        if (block != GeneratorModule.GENERATOR_CONTROLLER_BLOCK.get()) {
            stop();
            return;
        }
        volume = (float) (GeneratorConfig.BASE_GENERATOR_VOLUME.get() * (loop ? scaleDown : 1));
        if (loop && scaleDown > GeneratorConfig.LOOP_VOLUME_FACTOR.get()) {
            scaleDown -= 0.01f;
        }
    }

    protected boolean isSoundType(SoundEvent event){
        return sound == event;
    }

}
