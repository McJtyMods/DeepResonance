package mcjty.deepresonance.modules.machines.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CrystalizerData(int amount) {

    public static final CrystalizerData DEFAULT = new CrystalizerData(0);

    public static final Codec<CrystalizerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("amount").forGetter(CrystalizerData::amount)
    ).apply(instance, CrystalizerData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrystalizerData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CrystalizerData::amount,
            CrystalizerData::new
    );

    public CrystalizerData withAmount(int amount) {
        return new CrystalizerData(amount);
    }
}
