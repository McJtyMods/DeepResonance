package mcjty.deepresonance.modules.radiation.client;

import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import com.mojang.serialization.MapCodec;
import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;

public record RadiationMonitorProperty() implements RangeSelectItemModelProperty {

    private static final MapCodec<RadiationMonitorProperty> CODEC = MapCodec.unit(new RadiationMonitorProperty());

    public static void register() {
        RangeSelectItemModelProperties.ID_MAPPER.put(RadiationMonitorItem.RADIATION_PROPERTY, CODEC);
    }

    @Override
    public float get(ItemStack stack, ClientLevel level, ItemOwner owner, int seed) {
        RadiationMonitorItem.fetchRadiation(Minecraft.getInstance().player);
        return (float) Math.clamp(10 * RadiationMonitorItem.radiationStrength / RadiationConfiguration.MAX_RADIATION_METER.get(), 0, 9);
    }

    @Override
    public MapCodec<RadiationMonitorProperty> type() {
        return CODEC;
    }
}
