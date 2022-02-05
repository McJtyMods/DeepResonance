package mcjty.deepresonance.modules.generator.sound;

import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
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

        ClientPlayerEntity player = Minecraft.getInstance().player;
        double distance = Math.sqrt(this.pos.distSqr(player.getX(), player.getY(), player.getZ(), true));
        if (distance > 20) {
            volume = 0;
        } else {
            volume = (float) (GeneratorConfig.BASE_GENERATOR_VOLUME.get() * (20-distance)/20.0);
        }

//        volume = (float) (GeneratorConfig.BASE_GENERATOR_VOLUME.get() * (loop ? scaleDown : 1));
//        if (loop && scaleDown > GeneratorConfig.LOOP_VOLUME_FACTOR.get()) {
//            scaleDown -= 0.01f;
//        }

    }

    protected boolean isSoundType(SoundEvent event){
        return sound == event;
    }

}
