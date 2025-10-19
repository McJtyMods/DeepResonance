package mcjty.deepresonance.modules.generator.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

// This is used when the block is broken so the remaining energy can be stored in the drop
public record GeneratorPartData(int preservedEnergy) {

    public static final GeneratorPartData DEFAULT = new GeneratorPartData(0);

    public static final Codec<GeneratorPartData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("energy").forGetter(GeneratorPartData::preservedEnergy)
    ).apply(instance, GeneratorPartData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GeneratorPartData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, GeneratorPartData::preservedEnergy,
            GeneratorPartData::new
    );

    public GeneratorPartData withPreservedEnergy(int preservedEnergy) {
        return new GeneratorPartData(preservedEnergy);
    }
}
