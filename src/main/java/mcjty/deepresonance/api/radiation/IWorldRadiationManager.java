package mcjty.deepresonance.api.radiation;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IWorldRadiationManager {

    void removeAllRadiation();

    @Nonnull
    IRadiationSource getOrCreateRadiationSource(BlockPos coordinate);

    @Nullable
    IRadiationSource getRadiationSource(BlockPos coordinate);

}
