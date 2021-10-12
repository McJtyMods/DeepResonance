package mcjty.deepresonance.api.crystal;

import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;

public interface ICrystalModifier {

    void setCrystal(ResonatingCrystalTileEntity crystal);

    void tick();

    void onPowerChanged(boolean isEmpty);

    float getPowerModifier(float percentage, boolean simulate);

    float getRadiationModifier();

}
