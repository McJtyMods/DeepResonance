package mcjty.deepresonance.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LCD(double quality, double purity, double strength, double efficiency) {

    public static final LCD DEFAULT = new LCD(0.0, 0.0, 0.0, 0.0);

    public static final Codec<LCD> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("quality").forGetter(LCD::quality),
            Codec.DOUBLE.fieldOf("purity").forGetter(LCD::purity),
            Codec.DOUBLE.fieldOf("strength").forGetter(LCD::strength),
            Codec.DOUBLE.fieldOf("efficiency").forGetter(LCD::efficiency)
    ).apply(instance, LCD::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LCD> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, LCD::quality,
            ByteBufCodecs.DOUBLE, LCD::purity,
            ByteBufCodecs.DOUBLE, LCD::strength,
            ByteBufCodecs.DOUBLE, LCD::efficiency,
            LCD::new
    );

    public LCD withQuality(double quality) {
        return new LCD(quality, this.purity, this.strength, this.efficiency);
    }

    public LCD withPurity(double purity) {
        return new LCD(this.quality, purity, this.strength, this.efficiency);
    }

    public LCD withStrength(double strength) {
        return new LCD(this.quality, this.purity, strength, this.efficiency);
    }

    public LCD withEfficiency(double efficiency) {
        return new LCD(this.quality, this.purity, this.strength, efficiency);
    }
}
