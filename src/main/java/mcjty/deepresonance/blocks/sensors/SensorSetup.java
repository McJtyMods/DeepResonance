package mcjty.deepresonance.blocks.sensors;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SensorSetup {
    public static CooldownSensorBlock cooldownSensorBlock;
    public static InstabilitySensorBlock instabilitySensorBlock;

    public static void setupBlocks() {
        cooldownSensorBlock = new CooldownSensorBlock();
        instabilitySensorBlock = new InstabilitySensorBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        cooldownSensorBlock.initModel();
        instabilitySensorBlock.initModel();
    }
}
