package mcjty.deepresonance.integration.computers;

import mcjty.lib.integration.computers.OcCompatTools;
import net.minecraftforge.fml.common.Optional;

public class OpenComputersIntegration {
    @Optional.Method(modid="opencomputers")
    public static void init() {
        OcCompatTools.driverAdd(new TankDriver.OCDriver());
        OcCompatTools.driverAdd(new ValveDriver.OCDriver());
        OcCompatTools.driverAdd(new LaserDriver.OCDriver());
        OcCompatTools.driverAdd(new SmelterDriver.OCDriver());
        OcCompatTools.driverAdd(new CrystalizerDriver.OCDriver());
        OcCompatTools.driverAdd(new GeneratorControllerDriver.OCDriver());
        OcCompatTools.driverAdd(new PedestalDriver.OCDriver());
    }
}
