package mcjty.deepresonance.modules.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import static mcjty.deepresonance.DeepResonance.MODID;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import mcjty.deepresonance.modules.worldgen.world.ResonantCrystalFeature;
import mcjty.deepresonance.modules.worldgen.world.ResonantCrystalFeatureConfig;
import mcjty.lib.modules.IModule;

import java.util.function.Supplier;

import static mcjty.deepresonance.setup.Registration.FEATURES;

public class WorldGenModule implements IModule {

    public static final Supplier<ResonantCrystalFeature> CRYSTAL_FEATURE = FEATURES.register(
            ResonantCrystalFeature.FEATURE_CRYSTAL_ID.getPath(),
            () -> new ResonantCrystalFeature(ResonantCrystalFeatureConfig.CODEC));

    @Override
    public void init() {
        addFeature(BiomeTags.IS_OVERWORLD, GenerationStep.Decoration.RAW_GENERATION, "configured_crystal_overworld");
        addFeature(BiomeTags.IS_NETHER, GenerationStep.Decoration.RAW_GENERATION, "configured_crystal_nether");
        addFeature(BiomeTags.IS_END, GenerationStep.Decoration.RAW_GENERATION, "configured_crystal_end");
        addFeature(BiomeTags.IS_OVERWORLD, GenerationStep.Decoration.UNDERGROUND_ORES, "resonating_overworld");
        addFeature(BiomeTags.IS_OVERWORLD, GenerationStep.Decoration.UNDERGROUND_ORES, "resonating_overworld_ds");
        addFeature(BiomeTags.IS_NETHER, GenerationStep.Decoration.UNDERGROUND_ORES, "resonating_nether");
        addFeature(BiomeTags.IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, "resonating_end");
    }

    private static void addFeature(TagKey<Biome> biomes, GenerationStep.Decoration step, String feature) {
        BiomeModifications.addFeature(BiomeSelectors.tag(biomes), step,
                ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(MODID, feature)));
    }

    @Override
    public void initClient() {

    }

    @Override
    public void initConfig() {
        WorldGenConfiguration.init();
    }
}
