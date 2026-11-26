package mcjty.deepresonance;

import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = DeepResonance.MODID)
public final class ResonanceTestRegistration {
    private static final Identifier PURIFIER = id("purifier_and_lasers");
    private static final Identifier GENERATOR = id("shielded_generator");

    private static final Identifier SETUP_PURIFICATION = id("testsetup_purification");
    private static final Identifier SETUP_GENERATION = id("testsetup_generation");
    private static final Identifier SETUP_CRYSTAL = id("testsetup_crystal");
    private static final Identifier SETUP_SMELTER = id("testsetup_smelter");

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(DeepResonance.MODID, path);
    }

    @SubscribeEvent
    public static void registerFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, PURIFIER, () -> ResonanceTests::purifierAndBothLasersChangeRcl);
        event.register(Registries.TEST_FUNCTION, GENERATOR, () -> ResonanceTests::crystalsGenerateRfInsideRadiationShield);
        event.register(Registries.TEST_FUNCTION, SETUP_PURIFICATION, () -> ResonanceTestSetup::purification);
        event.register(Registries.TEST_FUNCTION, SETUP_GENERATION, () -> ResonanceTestSetup::generation);
        event.register(Registries.TEST_FUNCTION, SETUP_CRYSTAL, () -> ResonanceTestSetup::crystal);
        event.register(Registries.TEST_FUNCTION, SETUP_SMELTER, () -> ResonanceTestSetup::smelter);
    }

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        var environment = event.registerEnvironment(id("mod_lab"));
        for (Identifier test : new Identifier[]{PURIFIER, GENERATOR, SETUP_PURIFICATION, SETUP_GENERATION, SETUP_CRYSTAL, SETUP_SMELTER}) {
            event.registerTest(test, data -> new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, test), data),
                    new TestData<>(environment, id("station"), 400, 0, true));
        }
    }
}
