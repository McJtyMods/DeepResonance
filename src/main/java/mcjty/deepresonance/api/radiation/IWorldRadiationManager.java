package mcjty.deepresonance.api.radiation;

import net.minecraft.util.math.BlockPos;

/**
 * Created by Elec332 on 12-7-2020
 */
public interface IWorldRadiationManager {

    void removeAllRadiation();

    IRadiationSource getOrCreateRadiationSource(BlockPos coordinate);

    IRadiationSource getRadiationSource(BlockPos coordinate);

}
