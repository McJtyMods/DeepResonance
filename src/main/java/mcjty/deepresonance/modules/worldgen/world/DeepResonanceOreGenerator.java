package mcjty.deepresonance.modules.worldgen.world;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import mcjty.rftoolsbase.setup.Registration;
import mcjty.rftoolsbase.worldgen.CountPlacementConfig;
import mcjty.rftoolsbase.worldgen.DimensionCompositeFeature;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.function.Predicate;

public class DeepResonanceOreGenerator {

    public static ConfiguredFeature OVERWORLD_RESONATING_ORE;
    public static ConfiguredFeature NETHER_RESONATING_ORE;
    public static ConfiguredFeature END_RESONATING_ORE;

    public static final RuleTest ENDSTONE_TEST = new TagMatchRuleTest(Tags.Blocks.END_STONES);

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

        ConfiguredFeature<OreFeatureConfig, ?> overworldFeature = Feature.ORE
                .configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, CoreModule.RESONATING_ORE_STONE_BLOCK.get().defaultBlockState(),
                        WorldGenConfiguration.OVERWORLD_VEIN_SIZE.get()));
        OVERWORLD_RESONATING_ORE = new DimensionCompositeFeature(overworldFeature, World.OVERWORLD)
                .decorated(Registration.COUNT_PLACEMENT.get().configured(new CountPlacementConfig(
                        WorldGenConfiguration.OVERWORLD_MINY.get(),
                        0,
                        WorldGenConfiguration.OVERWORLD_MAXY.get() - WorldGenConfiguration.OVERWORLD_MINY.get(),
                        WorldGenConfiguration.OVERWORLD_SPAWN_CHANCES.get())));
        Registry.register(registry, new ResourceLocation(DeepResonance.MODID, "resonating_overworld"), OVERWORLD_RESONATING_ORE);

        ConfiguredFeature<OreFeatureConfig, ?> netherFeature = Feature.ORE
                .configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHER_ORE_REPLACEABLES, CoreModule.RESONATING_ORE_NETHER_BLOCK.get().defaultBlockState(),
                        WorldGenConfiguration.NETHER_VEIN_SIZE.get()));
        NETHER_RESONATING_ORE = new DimensionCompositeFeature(netherFeature, World.NETHER)
                .decorated(Registration.COUNT_PLACEMENT.get().configured(new CountPlacementConfig(
                        WorldGenConfiguration.NETHER_MINY.get(),
                        0,
                        WorldGenConfiguration.NETHER_MAXY.get() - WorldGenConfiguration.NETHER_MINY.get(),
                        WorldGenConfiguration.NETHER_SPAWN_CHANCES.get())));
        Registry.register(registry, new ResourceLocation(DeepResonance.MODID, "resonating_nether"), NETHER_RESONATING_ORE);

        ConfiguredFeature<OreFeatureConfig, ?> endFeature = Feature.ORE
                .configured(new OreFeatureConfig(ENDSTONE_TEST, CoreModule.RESONATING_ORE_END_BLOCK.get().defaultBlockState(),
                        WorldGenConfiguration.END_VEIN_SIZE.get()));
        END_RESONATING_ORE = new DimensionCompositeFeature(endFeature, World.END)
                .decorated(Registration.COUNT_PLACEMENT.get().configured(new CountPlacementConfig(
                        WorldGenConfiguration.END_MINY.get(),
                        0,
                        WorldGenConfiguration.END_MAXY.get() - WorldGenConfiguration.END_MINY.get(),
                        WorldGenConfiguration.END_SPAWN_CHANCES.get())));
        Registry.register(registry, new ResourceLocation(DeepResonance.MODID, "resonating_end"), END_RESONATING_ORE);
    }

    public static void onBiomeLoadingEvent(BiomeLoadingEvent event) {
        if (event.getCategory() == Biome.Category.NETHER) {
            event.getGeneration().getFeatures(GenerationStage.Decoration.RAW_GENERATION).add(() -> ResonantCrystalFeature.CRYSTAL_CONFIGURED_NETHER);
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, NETHER_RESONATING_ORE);
        } else if (event.getCategory() == Biome.Category.THEEND) {
            event.getGeneration().getFeatures(GenerationStage.Decoration.RAW_GENERATION).add(() -> ResonantCrystalFeature.CRYSTAL_CONFIGURED_END);
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, END_RESONATING_ORE);
        } else {
            event.getGeneration().getFeatures(GenerationStage.Decoration.RAW_GENERATION).add(() -> ResonantCrystalFeature.CRYSTAL_CONFIGURED_OVERWORLD);
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, OVERWORLD_RESONATING_ORE);
        }
    }
}
