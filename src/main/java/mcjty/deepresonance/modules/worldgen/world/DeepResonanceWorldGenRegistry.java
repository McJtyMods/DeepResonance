package mcjty.deepresonance.modules.worldgen.world;

import com.google.common.base.Preconditions;
import elec332.core.api.registration.IWorldGenRegister;
import elec332.core.api.world.IBiomeGenWrapper;
import elec332.core.api.world.RetroGenFeatureWrapper;
import elec332.core.world.FeaturePlacers;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.worldgen.util.WorldGenConfiguration;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
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
    private final WorldGenConfiguration configuration;

    private static final OreFeatureConfig.FillerBlockType STONE, NETHERRACK, END_STONE;
    private Feature<OreFeatureConfig> RESONATING_ORE;
    private Feature<ResonantCrystalFeatureConfig> RESONANT_CRYSTAL;

    public DeepResonanceWorldGenRegistry(WorldGenConfiguration configuration) {
        this.configuration = configuration;
    }

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
        if (!WorldGenConfiguration.ORE_BLACKLIST.get().contains(Preconditions.checkNotNull(biome.getRegistryName()).toString())) {
            if (biome.getCategory() == Biome.Category.NETHER && WorldGenConfiguration.NETHER_ORE.get()) {
                registry.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                        RESONATING_ORE.withConfiguration(new OreFeatureConfig(NETHERRACK, CoreModule.RESONATING_ORE_NETHER_BLOCK.get().getDefaultState(), WorldGenConfiguration.VEIN_SIZE.get())),
                        FeaturePlacers.COUNT_RANGE.configure(new CountRangeConfig(WorldGenConfiguration.SPAWN_CHANCES.get(), WorldGenConfiguration.MIN_Y.get() * 2, WorldGenConfiguration.MIN_Y.get() * 2, WorldGenConfiguration.MAX_Y.get() * 2)));
            } else if (biome.getCategory() == Biome.Category.THEEND && WorldGenConfiguration.END_ORE.get()) {
                registry.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                        RESONATING_ORE.withConfiguration(new OreFeatureConfig(END_STONE, CoreModule.RESONATING_ORE_END_BLOCK.get().getDefaultState(), WorldGenConfiguration.VEIN_SIZE.get())),
                        FeaturePlacers.COUNT_RANGE.configure(new CountRangeConfig(WorldGenConfiguration.SPAWN_CHANCES.get(), WorldGenConfiguration.MIN_Y.get(), WorldGenConfiguration.MIN_Y.get(), WorldGenConfiguration.MAX_Y.get())));
            } else if (WorldGenConfiguration.OTHER_ORE.get()) {
                registry.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                        RESONATING_ORE.withConfiguration(new OreFeatureConfig(STONE, CoreModule.RESONATING_ORE_STONE_BLOCK.get().getDefaultState(), WorldGenConfiguration.VEIN_SIZE.get())),
                        FeaturePlacers.COUNT_RANGE.configure(new CountRangeConfig(WorldGenConfiguration.SPAWN_CHANCES.get(), WorldGenConfiguration.MIN_Y.get(), WorldGenConfiguration.MIN_Y.get(), WorldGenConfiguration.MAX_Y.get())));
            }
        }
        if (!WorldGenConfiguration.CRYSTAL_BLACKLIST.get().contains(Preconditions.checkNotNull(biome.getRegistryName()).toString())) {
            if (biome.getCategory() == Biome.Category.NETHER && WorldGenConfiguration.NETHER_CRYSTALS.get()) {
                registry.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                        RESONANT_CRYSTAL.withConfiguration(new ResonantCrystalFeatureConfig("resonant_crystal_nether", 3.0f, 1.5f, 2.0f, 0.5f)));
            } else if (WorldGenConfiguration.OTHER_CRYSTALS.get()) {
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
