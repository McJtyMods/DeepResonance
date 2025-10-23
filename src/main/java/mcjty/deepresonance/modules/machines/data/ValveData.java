package mcjty.deepresonance.modules.machines.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ValveData(float minPurity, float minStrength, float minEfficiency, int maxMb) {

    public static final ValveData DEFAULT = new ValveData(1.0f, 1.0f, 1.0f, 0);

    public static final Codec<ValveData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("minpurity").forGetter(ValveData::minPurity),
            Codec.FLOAT.fieldOf("minstrength").forGetter(ValveData::minStrength),
            Codec.FLOAT.fieldOf("minefficiency").forGetter(ValveData::minEfficiency),
            Codec.INT.fieldOf("maxmb").forGetter(ValveData::maxMb)
    ).apply(instance, ValveData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ValveData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ValveData::minPurity,
            ByteBufCodecs.FLOAT, ValveData::minStrength,
            ByteBufCodecs.FLOAT, ValveData::minEfficiency,
            ByteBufCodecs.INT, ValveData::maxMb,
            ValveData::new
    );

    public ValveData withMinPurity(float minPurity) {
        return new ValveData(minPurity, minStrength, minEfficiency, maxMb);
    }

    public ValveData withMinStrength(float minStrength) {
        return new ValveData(minPurity, minStrength, minEfficiency, maxMb);
    }

    public ValveData withMinEfficiency(float minEfficiency) {
        return new ValveData(minPurity, minStrength, minEfficiency, maxMb);
    }

    public ValveData withMaxMb(int maxMb) {
        return new ValveData(minPurity, minStrength, minEfficiency, maxMb);
    }


}
