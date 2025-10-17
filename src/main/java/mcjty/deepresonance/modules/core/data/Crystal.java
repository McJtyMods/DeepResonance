package mcjty.deepresonance.modules.core.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Crystal(double power, double purity, double strength, double efficiency) {

    public static final Crystal DEFAULT = new Crystal(1.0, 1.0, 1.0, 1.0);
    public static final Crystal ZERO = new Crystal(0.0, 0.0, 0.0, 0.0);

    public static final Codec<Crystal> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("power").forGetter(Crystal::power),
            Codec.DOUBLE.fieldOf("purity").forGetter(Crystal::purity),
            Codec.DOUBLE.fieldOf("strength").forGetter(Crystal::strength),
            Codec.DOUBLE.fieldOf("efficiency").forGetter(Crystal::efficiency)
    ).apply(instance, Crystal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Crystal> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, Crystal::power,
            ByteBufCodecs.DOUBLE, Crystal::purity,
            ByteBufCodecs.DOUBLE, Crystal::strength,
            ByteBufCodecs.DOUBLE, Crystal::efficiency,
            Crystal::new
    );

    public Crystal withPower(double power) {
        return new Crystal(power, this.purity, this.strength, this.efficiency);
    }

    public Crystal withPurity(double purity) {
        return new Crystal(this.power, purity, this.strength, this.efficiency);
    }

    public Crystal withStrength(double strength) {
        return new Crystal(this.power, this.purity, strength, this.efficiency);
    }

    public Crystal withEfficiency(double efficiency) {
        return new Crystal(this.power, this.purity, this.strength, efficiency);
    }
}
