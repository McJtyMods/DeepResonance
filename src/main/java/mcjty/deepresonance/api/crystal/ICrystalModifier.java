package mcjty.deepresonance.api.crystal;

import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;

/**
 * Created by Elec332 on 31-7-2020
 */
public interface ICrystalModifier {

    void setCrystal(TileEntityResonatingCrystal crystal);

    void tick();

    void onPowerChanged(boolean isEmpty);

    float getPowerModifier(float percentage, boolean simulate);

    float getRadiationModifier();

}
