package mcjty.deepresonance.modules.radiation.manager;

import mcjty.deepresonance.modules.radiation.util.RadiationShieldRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public final class RadiationShieldUpdates {

    private RadiationShieldUpdates() {
    }

    public static void onBlockChanged(ServerLevel level, BlockPos pos, BlockState previous, BlockState current) {
        if (previous.getBlock() == current.getBlock()) {
            return;
        }
        double blocker = RadiationShieldRegistry.getBlocker(current);
        if (blocker == RadiationShieldRegistry.getBlocker(previous)) {
            return;
        }
        DRRadiationManager.getManager(level).getRadiationSources().forEach((globalPos, source) -> {
            BlockPos center = globalPos.pos();
            float radius = source.getRadius();
            if (globalPos.dimension().equals(level.dimension())
                    && Math.abs(center.getX() - pos.getX()) < radius
                    && Math.abs(center.getY() - pos.getY()) < radius
                    && Math.abs(center.getZ() - pos.getZ()) < radius) {
                source.getRadiationTree(level, center.getX(), center.getY(), center.getZ())
                        .addBlocker(pos.getX(), pos.getY(), pos.getZ(), (float) blocker);
            }
        });
    }
}
