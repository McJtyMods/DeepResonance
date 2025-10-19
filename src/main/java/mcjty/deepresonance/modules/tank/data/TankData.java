package mcjty.deepresonance.modules.tank.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

// This is used when the block is broken so the remaining energy can be stored in the drop
public record TankData(FluidStack preservedLiquid) {

    public static final TankData DEFAULT = new TankData(FluidStack.EMPTY);

    public static final Codec<TankData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidStack.CODEC.fieldOf("liquid").forGetter(TankData::preservedLiquid)
    ).apply(instance, TankData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TankData> STREAM_CODEC = StreamCodec.composite(
            FluidStack.OPTIONAL_STREAM_CODEC, TankData::preservedLiquid,
            TankData::new
    );

    public TankData withPreservedLiquid(FluidStack preservedLiquid) {
        return new TankData(preservedLiquid);
    }
}
