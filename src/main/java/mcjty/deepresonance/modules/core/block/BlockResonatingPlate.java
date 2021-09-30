package mcjty.deepresonance.modules.core.block;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockResonatingPlate extends Block {

    public BlockResonatingPlate(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return CoreModule.resonatingPlateConfig.RADIATION_STRENGTH.get() > 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(@Nonnull BlockState state, @Nonnull ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
        if (!isRandomlyTicking(state)) {
            return;
        }
        if (worldIn.getDirectSignalTo(pos) > 0) {
            worldIn.getCapability(RadiationModule.CAPABILITY).ifPresent(radiationManager ->
                    radiationManager.getOrCreateRadiationSource(pos).update(CoreModule.resonatingPlateConfig.RADIATION_RADIUS.get(), CoreModule.resonatingPlateConfig.RADIATION_STRENGTH.get(), CoreModule.resonatingPlateConfig.RADIATION_TICKS.get()));
        }
    }

}
