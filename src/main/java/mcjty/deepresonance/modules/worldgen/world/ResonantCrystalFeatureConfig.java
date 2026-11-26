package mcjty.deepresonance.modules.worldgen.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ResonantCrystalFeatureConfig {

    public static final MapCodec<ResonantCrystalFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("strength").forGetter(ResonantCrystalFeatureConfig::getStrength),
                    Codec.FLOAT.fieldOf("power").forGetter(ResonantCrystalFeatureConfig::getPower),
                    Codec.FLOAT.fieldOf("efficiency").forGetter(ResonantCrystalFeatureConfig::getEfficiency),
                    Codec.FLOAT.fieldOf("purity").forGetter(ResonantCrystalFeatureConfig::getPurity)
            ).apply(instance, ResonantCrystalFeatureConfig::new));

    public final float strength;
    public final float power;
    public final float efficiency;
    public final float purity;

    public ResonantCrystalFeatureConfig(float strength, float power, float efficiency, float purity) {
        this.strength = strength;
        this.power = power;
        this.efficiency = efficiency;
        this.purity = purity;
    }

    public float getStrength() {
        return strength;
    }

    public float getPower() {
        return power;
    }

    public float getEfficiency() {
        return efficiency;
    }

    public float getPurity() {
        return purity;
    }
}
