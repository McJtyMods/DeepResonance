package mcjty.deepresonance.modules.machines.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

// This is used when the block is broken so the remaining energy can be stored in the drop
public record LaserData(float crystalLiquid) {

    public static final LaserData DEFAULT = new LaserData(0);

    public static final Codec<LaserData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("liquid").forGetter(LaserData::crystalLiquid)
    ).apply(instance, LaserData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LaserData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, LaserData::crystalLiquid,
            LaserData::new
    );

    public LaserData withCrystalLiquid(float crystalLiquid) {
        return new LaserData(crystalLiquid);
    }
}
