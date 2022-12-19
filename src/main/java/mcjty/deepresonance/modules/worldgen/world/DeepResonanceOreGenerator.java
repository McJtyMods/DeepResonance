package mcjty.deepresonance.modules.worldgen.world;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import mcjty.deepresonance.setup.Registration;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class DeepResonanceOreGenerator {

    public static void init() {
    }

    public static final RuleTest ENDSTONE_TEST = new TagMatchTest(Tags.Blocks.END_STONES);

    public static final RegistryObject<ConfiguredFeature<?, ?>> OVERWORLD_RESONATING_ORE = Registration.CONFIGURED_FEATURES.register(
            "resonating_overworld",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(OreFeatures.STONE_ORE_REPLACEABLES, CoreModule.RESONATING_ORE_STONE_BLOCK.get().defaultBlockState(),
                    4)));
    public static final RegistryObject<PlacedFeature> PLACEMENT_OVERWORLD_RESONATING_ORE = Registration.PLACED_FEATURES.register(
            "resonating_overworld",
            () -> new PlacedFeature(OVERWORLD_RESONATING_ORE.getHolder().get(), List.of(
                    CountPlacement.of(7),
                    InSquarePlacement.spread(),
                    BiomeFilter.biome(),
                    HeightRangePlacement.uniform(VerticalAnchor.absolute(-60), VerticalAnchor.absolute(32))
            )));

    public static final RegistryObject<ConfiguredFeature<?, ?>> OVERWORLDBS_RESONATING_ORE = Registration.CONFIGURED_FEATURES.register(
            "resonating_overworld_ds",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, CoreModule.RESONATING_ORE_DEEPSLATE_BLOCK.get().defaultBlockState(),
                    4)));
    public static final RegistryObject<PlacedFeature> PLACEMENT_OVERWORLDBS_RESONATING_ORE = Registration.PLACED_FEATURES.register(
            "resonating_overworld_ds",
            () -> new PlacedFeature(OVERWORLDBS_RESONATING_ORE.getHolder().get(), List.of(
                    CountPlacement.of(7),
                    InSquarePlacement.spread(),
                    BiomeFilter.biome(),
                    HeightRangePlacement.uniform(VerticalAnchor.absolute(-60), VerticalAnchor.absolute(32))
            )));

    public static final RegistryObject<ConfiguredFeature<?, ?>> NETHER_RESONATING_ORE = Registration.CONFIGURED_FEATURES.register(
            "resonating_nether",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(OreFeatures.NETHER_ORE_REPLACEABLES, CoreModule.RESONATING_ORE_NETHER_BLOCK.get().defaultBlockState(),
                    8)));
    public static final RegistryObject<PlacedFeature> PLACEMENT_NETHER_RESONATING_ORE = Registration.PLACED_FEATURES.register(
            "resonating_nether",
            () -> new PlacedFeature(NETHER_RESONATING_ORE.getHolder().get(), List.of(
                    CountPlacement.of(12),
                    InSquarePlacement.spread(),
                    BiomeFilter.biome(),
                    HeightRangePlacement.uniform(VerticalAnchor.absolute(2), VerticalAnchor.absolute(32))
            )));

    public static final RegistryObject<ConfiguredFeature<?, ?>> END_RESONATING_ORE = Registration.CONFIGURED_FEATURES.register(
            "resonating_end",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ENDSTONE_TEST, CoreModule.RESONATING_ORE_END_BLOCK.get().defaultBlockState(),
                    5)));
    public static final RegistryObject<PlacedFeature> PLACEMENT_END_RESONATING_ORE = Registration.PLACED_FEATURES.register(
            "resonating_end",
            () -> new PlacedFeature(END_RESONATING_ORE.getHolder().get(), List.of(
                    CountPlacement.of(8),
                    InSquarePlacement.spread(),
                    BiomeFilter.biome(),
                    HeightRangePlacement.uniform(VerticalAnchor.absolute(2), VerticalAnchor.absolute(32))
            )));
}
