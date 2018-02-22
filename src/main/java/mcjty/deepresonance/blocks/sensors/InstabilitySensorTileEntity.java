package mcjty.deepresonance.blocks.sensors;

import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.radiation.SuperGenerationConfiguration;

public class InstabilitySensorTileEntity extends AbstractSensorTileEntity {

    @Override
    protected int checkSensor() {
        ResonatingCrystalTileEntity crystal = getCrystal();
        if (crystal != null) {
            float instability = crystal.getInstability();
            return Math.min(15, (int) (15.0f * instability / SuperGenerationConfiguration.instabilitySensorThresshold));
        }
        return 0;
    }
}
