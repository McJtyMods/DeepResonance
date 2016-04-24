package mcjty.deepresonance.client.sound;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.gencontroller.GeneratorControllerSetup;
import mcjty.deepresonance.blocks.generator.GeneratorConfiguration;
import net.minecraft.block.Block;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 21-4-2016.
 */
@SideOnly(Side.CLIENT)
public class GeneratorSound extends MovingSound {

    public GeneratorSound(SoundEvent event, World world, BlockPos pos){
        super(event, SoundCategory.BLOCKS);
        this.world = world;
        this.pos = pos;
        this.xPosF = pos.getX();
        this.yPosF = pos.getY();
        this.zPosF = pos.getZ();
        this.attenuationType = AttenuationType.LINEAR;
        this.repeat = true;
        this.repeatDelay = 0;
        this.loop = event == GeneratorSoundController.loop;
        this.sound = event;
    }

    private final World world;
    private final BlockPos pos;
    private final boolean loop;
    private final SoundEvent sound;
    private float scaleDown = 1.0f;

    @Override
    public void update() {
        Block block = WorldHelper.getBlockAt(world, pos);
        if (block != GeneratorControllerSetup.generatorControllerBlock) {
            donePlaying = true;
            return;
        }
        volume = GeneratorConfiguration.baseGeneratorVolume * (loop ? scaleDown : 1);
        if (loop && scaleDown > GeneratorConfiguration.loopVolumeFactor) {
            scaleDown -= 0.01f;
        }
    }

    protected boolean isSoundType(SoundEvent event){
        return sound == event;
    }

}
