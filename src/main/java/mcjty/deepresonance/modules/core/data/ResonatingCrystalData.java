package mcjty.deepresonance.modules.core.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ResonatingCrystalData(boolean glowing) {

    public static final ResonatingCrystalData DEFAULT = new ResonatingCrystalData(false);

    public static final Codec<ResonatingCrystalData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("glowing").forGetter(ResonatingCrystalData::glowing)
    ).apply(instance, ResonatingCrystalData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResonatingCrystalData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ResonatingCrystalData::glowing,
            ResonatingCrystalData::new
    );

    public ResonatingCrystalData withGlowing(boolean glowing) {
        return new ResonatingCrystalData(glowing);
    }
}
