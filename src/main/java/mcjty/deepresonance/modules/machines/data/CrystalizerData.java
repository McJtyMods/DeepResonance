package mcjty.deepresonance.modules.machines.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

// This is used when the block is broken so the remaining energy can be stored in the drop
public record CrystalizerData(int amount, int progress) {

    public static final CrystalizerData DEFAULT = new CrystalizerData(0, 0);

    public static final Codec<CrystalizerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("amount").forGetter(CrystalizerData::amount),
            Codec.INT.fieldOf("progress").forGetter(CrystalizerData::progress)
    ).apply(instance, CrystalizerData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrystalizerData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CrystalizerData::amount,
            ByteBufCodecs.INT, CrystalizerData::progress,
            CrystalizerData::new
    );

    public CrystalizerData withAmount(int amount) {
        return new CrystalizerData(amount, this.progress);
    }

    public CrystalizerData withProgress(int progress) {
        return new CrystalizerData(this.amount, progress);
    }
}
