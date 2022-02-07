package mcjty.deepresonance.modules.worldgen.world;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class DeepResonanceOreGenerator {

    public static PlacedFeature OVERWORLDBS_RESONATING_ORE;
    public static PlacedFeature OVERWORLD_RESONATING_ORE;
    public static PlacedFeature NETHER_RESONATING_ORE;
    public static PlacedFeature END_RESONATING_ORE;

    public static final RuleTest ENDSTONE_TEST = new TagMatchTest(Tags.Blocks.END_STONES);

    public static void registerConfiguredFeatures() {
        OreConfiguration overworldConfig = new OreConfiguration(OreFeatures.STONE_ORE_REPLACEABLES, CoreModule.RESONATING_ORE_STONE_BLOCK.get().defaultBlockState(),
                WorldGenConfiguration.OVERWORLD_VEIN_SIZE.get());
        OVERWORLD_RESONATING_ORE = registerPlacedFeature("resonating_overworld", Feature.ORE.configured(overworldConfig),
                CountPlacement.of(WorldGenConfiguration.OVERWORLD_SPAWN_CHANCES.get()),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(WorldGenConfiguration.OVERWORLD_MINY.get()), VerticalAnchor.absolute(WorldGenConfiguration.OVERWORLD_MAXY.get())));

        OreConfiguration deepslateConfig = new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, CoreModule.RESONATING_ORE_DEEPSLATE_BLOCK.get().defaultBlockState(),
                WorldGenConfiguration.OVERWORLD_VEIN_SIZE.get());
        OVERWORLDBS_RESONATING_ORE = registerPlacedFeature("resonating_overworld_ds", Feature.ORE.configured(deepslateConfig),
                CountPlacement.of(WorldGenConfiguration.OVERWORLD_SPAWN_CHANCES.get()),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(WorldGenConfiguration.OVERWORLD_MINY.get()), VerticalAnchor.absolute(WorldGenConfiguration.OVERWORLD_MAXY.get())));

        OreConfiguration netherConfig = new OreConfiguration(OreFeatures.NETHER_ORE_REPLACEABLES, CoreModule.RESONATING_ORE_NETHER_BLOCK.get().defaultBlockState(),
                WorldGenConfiguration.NETHER_VEIN_SIZE.get());
        NETHER_RESONATING_ORE = registerPlacedFeature("resonating_nether", Feature.ORE.configured(netherConfig),
                CountPlacement.of(WorldGenConfiguration.NETHER_SPAWN_CHANCES.get()),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(WorldGenConfiguration.NETHER_MINY.get()), VerticalAnchor.absolute(WorldGenConfiguration.NETHER_MAXY.get())));

        OreConfiguration endConfig = new OreConfiguration(ENDSTONE_TEST, CoreModule.RESONATING_ORE_END_BLOCK.get().defaultBlockState(),
                WorldGenConfiguration.END_VEIN_SIZE.get());
        END_RESONATING_ORE = registerPlacedFeature("resonating_end", Feature.ORE.configured(endConfig),
                CountPlacement.of(WorldGenConfiguration.END_SPAWN_CHANCES.get()),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(WorldGenConfiguration.END_MINY.get()), VerticalAnchor.absolute(WorldGenConfiguration.END_MAXY.get())));
    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> PlacedFeature registerPlacedFeature(String registryName, ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers) {
        PlacedFeature placed = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(registryName), feature).placed(placementModifiers);
        return PlacementUtils.register(registryName, placed);
    }

    public static void onBiomeLoadingEvent(BiomeLoadingEvent event) {
        if (event.getCategory() == Biome.BiomeCategory.NETHER) {
            event.getGeneration().addFeature(GenerationStep.Decoration.RAW_GENERATION, ResonantCrystalFeature.CRYSTAL_CONFIGURED_NETHER);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NETHER_RESONATING_ORE);
        } else if (event.getCategory() == Biome.BiomeCategory.THEEND) {
            event.getGeneration().addFeature(GenerationStep.Decoration.RAW_GENERATION, ResonantCrystalFeature.CRYSTAL_CONFIGURED_END);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, END_RESONATING_ORE);
        } else {
            event.getGeneration().addFeature(GenerationStep.Decoration.RAW_GENERATION, ResonantCrystalFeature.CRYSTAL_CONFIGURED_OVERWORLD);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OVERWORLD_RESONATING_ORE);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OVERWORLDBS_RESONATING_ORE);
        }
    }
}
