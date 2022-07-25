package mcjty.deepresonance.modules.worldgen.world;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.Tags;

public class DeepResonanceOreGenerator {

    public static Holder<PlacedFeature> OVERWORLDBS_RESONATING_ORE;
    public static Holder<PlacedFeature> OVERWORLD_RESONATING_ORE;
    public static Holder<PlacedFeature> NETHER_RESONATING_ORE;
    public static Holder<PlacedFeature> END_RESONATING_ORE;

    public static final RuleTest ENDSTONE_TEST = new TagMatchTest(Tags.Blocks.END_STONES);

    public static void registerConfiguredFeatures() {
        OreConfiguration overworldConfig = new OreConfiguration(OreFeatures.STONE_ORE_REPLACEABLES, CoreModule.RESONATING_ORE_STONE_BLOCK.get().defaultBlockState(),
                WorldGenConfiguration.OVERWORLD_VEIN_SIZE.get());
        OVERWORLD_RESONATING_ORE = registerPlacedFeature("resonating_overworld", new ConfiguredFeature<>(Feature.ORE, overworldConfig),
                CountPlacement.of(WorldGenConfiguration.OVERWORLD_SPAWN_CHANCES.get()),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(WorldGenConfiguration.OVERWORLD_MINY.get()), VerticalAnchor.absolute(WorldGenConfiguration.OVERWORLD_MAXY.get())));

        OreConfiguration deepslateConfig = new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, CoreModule.RESONATING_ORE_DEEPSLATE_BLOCK.get().defaultBlockState(),
                WorldGenConfiguration.OVERWORLD_VEIN_SIZE.get());
        OVERWORLDBS_RESONATING_ORE = registerPlacedFeature("resonating_overworld_ds", new ConfiguredFeature<>(Feature.ORE, deepslateConfig),
                CountPlacement.of(WorldGenConfiguration.OVERWORLD_SPAWN_CHANCES.get()),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(WorldGenConfiguration.OVERWORLD_MINY.get()), VerticalAnchor.absolute(WorldGenConfiguration.OVERWORLD_MAXY.get())));

        OreConfiguration netherConfig = new OreConfiguration(OreFeatures.NETHER_ORE_REPLACEABLES, CoreModule.RESONATING_ORE_NETHER_BLOCK.get().defaultBlockState(),
                WorldGenConfiguration.NETHER_VEIN_SIZE.get());
        NETHER_RESONATING_ORE = registerPlacedFeature("resonating_nether", new ConfiguredFeature<>(Feature.ORE, netherConfig),
                CountPlacement.of(WorldGenConfiguration.NETHER_SPAWN_CHANCES.get()),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(WorldGenConfiguration.NETHER_MINY.get()), VerticalAnchor.absolute(WorldGenConfiguration.NETHER_MAXY.get())));

        OreConfiguration endConfig = new OreConfiguration(ENDSTONE_TEST, CoreModule.RESONATING_ORE_END_BLOCK.get().defaultBlockState(),
                WorldGenConfiguration.END_VEIN_SIZE.get());
        END_RESONATING_ORE = registerPlacedFeature("resonating_end", new ConfiguredFeature<>(Feature.ORE, endConfig),
                CountPlacement.of(WorldGenConfiguration.END_SPAWN_CHANCES.get()),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(WorldGenConfiguration.END_MINY.get()), VerticalAnchor.absolute(WorldGenConfiguration.END_MAXY.get())));
    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> Holder<PlacedFeature> registerPlacedFeature(String registryName, ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers) {
        return PlacementUtils.register(registryName, Holder.direct(feature), placementModifiers);
    }
}
