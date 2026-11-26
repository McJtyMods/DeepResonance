package mcjty.deepresonance;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public final class ResonanceTestRegistration {

    @GameTest(structure = "deepresonance:station", maxTicks = 400)
    public void purifierAndBothLasersChangeRcl(GameTestHelper helper) {
        ResonanceTests.purifierAndBothLasersChangeRcl(helper);
    }

    @GameTest(structure = "deepresonance:station", maxTicks = 250)
    public void crystalsGenerateRfInsideRadiationShield(GameTestHelper helper) {
        ResonanceTests.crystalsGenerateRfInsideRadiationShield(helper);
    }

    @GameTest(structure = "deepresonance:station", maxTicks = 400)
    public void testSetupPurification(GameTestHelper helper) {
        ResonanceTestSetup.purification(helper);
    }

    @GameTest(structure = "deepresonance:station", maxTicks = 400)
    public void testSetupGeneration(GameTestHelper helper) {
        ResonanceTestSetup.generation(helper);
    }

    @GameTest(structure = "deepresonance:station", maxTicks = 400)
    public void testSetupCrystal(GameTestHelper helper) {
        ResonanceTestSetup.crystal(helper);
    }

    @GameTest(structure = "deepresonance:station", maxTicks = 400)
    public void testSetupSmelter(GameTestHelper helper) {
        ResonanceTestSetup.smelter(helper);
    }
}
