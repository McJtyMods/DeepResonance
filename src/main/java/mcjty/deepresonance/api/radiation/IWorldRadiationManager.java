package mcjty.deepresonance.api.radiation;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 12-7-2020
 */
public interface IWorldRadiationManager {

    void removeAllRadiation();

    @Nonnull
    IRadiationSource getOrCreateRadiationSource(BlockPos coordinate);

    @Nullable
    IRadiationSource getRadiationSource(BlockPos coordinate);

}
