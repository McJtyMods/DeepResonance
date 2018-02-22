package mcjty.deepresonance.blocks.sensors;

import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;

public class CooldownSensorTileEntity extends AbstractSensorTileEntity {

    @Override
    protected int checkSensor() {
        ResonatingCrystalTileEntity crystal = getCrystal();
        if (crystal != null) {
            float cooldown = crystal.getCooldown();
            return cooldown == 0 ? 15 : 0;
        }
        return 0;
    }

}
