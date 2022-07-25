package mcjty.deepresonance.modules.core.block;

import mcjty.deepresonance.modules.core.util.ResonatingPlateBlockConfig;
import mcjty.deepresonance.modules.radiation.manager.DRRadiationManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockResonatingPlate extends Block {

    public BlockResonatingPlate(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isRandomlyTicking(@Nonnull BlockState state) {
        return ResonatingPlateBlockConfig.RADIATION_STRENGTH.get() > 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(@Nonnull BlockState state, @Nonnull ServerLevel worldIn, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
        if (!isRandomlyTicking(state)) {
            return;
        }
        if (worldIn.getDirectSignalTo(pos) > 0) {
            DRRadiationManager.getManager(worldIn).getOrCreateRadiationSource(GlobalPos.of(worldIn.dimension(), pos)).update(ResonatingPlateBlockConfig.RADIATION_RADIUS.get(), ResonatingPlateBlockConfig.RADIATION_STRENGTH.get(), ResonatingPlateBlockConfig.RADIATION_TICKS.get());
        }
    }

}
