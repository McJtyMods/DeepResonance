package mcjty.deepresonance.modules.worldgen.world;

import com.google.common.base.Preconditions;
import elec332.core.api.registration.IWorldGenRegister;
import elec332.core.api.world.IBiomeGenWrapper;
import elec332.core.api.world.RetroGenFeatureWrapper;
import elec332.core.world.FeaturePlacers;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.worldgen.WorldGenModule;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockMatcher;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by Elec332 on 9-7-2020
 */
public class DeepResonanceWorldGenRegistry implements IWorldGenRegister {

    public boolean doRegister = false;

    private static final OreFeatureConfig.FillerBlockType STONE, NETHERRACK, END_STONE;
    private Feature<OreFeatureConfig> RESONATING_ORE;
    private Feature<ResonantCrystalFeatureConfig> RESONANT_CRYSTAL;

    @Override
    public void registerFeatures(IForgeRegistry<Feature<?>> featureRegistry) {
        featureRegistry.register(RESONATING_ORE = new RetroGenFeatureWrapper<>(Feature.ORE, new DeepResonanceResourceLocation("resonating_ore")));
        featureRegistry.register(RESONANT_CRYSTAL = new ResonantCrystalFeature());
    }

    @Override
    public void configureBiome(Biome biome, IBiomeGenWrapper registry) {
        if (!doRegister) {
            return;
        }
        if (!WorldGenModule.config.ore_blacklist.get().contains(Preconditions.checkNotNull(biome.getRegistryName()).toString())) {
            if (biome.getCategory() == Biome.Category.NETHER && WorldGenModule.config.nether_ore.get()) {
                registry.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                        RESONATING_ORE.withConfiguration(new OreFeatureConfig(NETHERRACK, CoreModule.RESONATING_ORE_NETHER_BLOCK.get().getDefaultState(), WorldGenModule.config.veinSize.get())),
                        FeaturePlacers.COUNT_RANGE.configure(new CountRangeConfig(WorldGenModule.config.spawnChances.get(), WorldGenModule.config.minY.get() * 2, WorldGenModule.config.minY.get() * 2, WorldGenModule.config.maxY.get() * 2)));
            } else if (biome.getCategory() == Biome.Category.THEEND && WorldGenModule.config.end_ore.get()) {
                registry.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                        RESONATING_ORE.withConfiguration(new OreFeatureConfig(END_STONE, CoreModule.RESONATING_ORE_END_BLOCK.get().getDefaultState(), WorldGenModule.config.veinSize.get())),
                        FeaturePlacers.COUNT_RANGE.configure(new CountRangeConfig(WorldGenModule.config.spawnChances.get(), WorldGenModule.config.minY.get(), WorldGenModule.config.minY.get(), WorldGenModule.config.maxY.get())));
            } else if (WorldGenModule.config.other_ore.get()) {
                registry.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                        RESONATING_ORE.withConfiguration(new OreFeatureConfig(STONE, CoreModule.RESONATING_ORE_STONE_BLOCK.get().getDefaultState(), WorldGenModule.config.veinSize.get())),
                        FeaturePlacers.COUNT_RANGE.configure(new CountRangeConfig(WorldGenModule.config.spawnChances.get(), WorldGenModule.config.minY.get(), WorldGenModule.config.minY.get(), WorldGenModule.config.maxY.get())));
            }
        }
        if (!WorldGenModule.config.crystal_blacklist.get().contains(Preconditions.checkNotNull(biome.getRegistryName()).toString())) {
            if (biome.getCategory() == Biome.Category.NETHER && WorldGenModule.config.nether_crystals.get()) {
                registry.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                        RESONANT_CRYSTAL.withConfiguration(new ResonantCrystalFeatureConfig("resonant_crystal_nether", 3.0f, 1.5f, 2.0f, 0.5f)));
            } else if (WorldGenModule.config.other_crystals.get()) {
                registry.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                        RESONANT_CRYSTAL.withConfiguration(new ResonantCrystalFeatureConfig("resonant_crystal_overworld", 1.0f, 1.0f, 1.0f, 1.0f)));
            }
        }
    }

    static {
        STONE = OreFeatureConfig.FillerBlockType.NATURAL_STONE;
        NETHERRACK = OreFeatureConfig.FillerBlockType.NETHERRACK;
        END_STONE = OreFeatureConfig.FillerBlockType.create("END_STONE", "end_stone", new BlockMatcher(Blocks.END_STONE));
    }

}
