package mcjty.deepresonance.modules.worldgen.world;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 10-7-2020
 */
public class ResonantCrystalFeatureConfig implements IFeatureConfig {

    public final String name;
    public final float strength, power, efficiency, purity;

    public ResonantCrystalFeatureConfig(String name, float strength, float power, float efficiency, float purity) {
        this.name = Preconditions.checkNotNull(name);
        this.strength = strength;
        this.power = power;
        this.efficiency = efficiency;
        this.purity = purity;
    }

    @Nonnull
    @Override
    public <T> Dynamic<T> serialize(@Nonnull DynamicOps<T> ops) {
        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(
                ops.createString("name"), ops.createString(this.name),
                ops.createString("strength"), ops.createFloat(this.strength),
                ops.createString("power"), ops.createFloat(this.power),
                ops.createString("efficiency"), ops.createFloat(this.efficiency),
                ops.createString("purity"), ops.createFloat(this.power)
        )));
    }

    public static ResonantCrystalFeatureConfig deserialize(Dynamic<?> in) {
        String name = in.get("name").asString().orElseThrow(NullPointerException::new);
        float s = in.get("strength").asFloat(1);
        float po = in.get("power").asFloat(1);
        float e = in.get("efficiency").asFloat(1);
        float pu = in.get("purity").asFloat(1);
        return new ResonantCrystalFeatureConfig(name, s, po, e, pu);
    }

}
