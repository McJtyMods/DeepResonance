package mcjty.deepresonance.fluids;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 7-1-2020
 */
public class FluidLiquidCrystal extends EmptyFluid {

    private static final ResourceLocation texture = new DeepResonanceResourceLocation("blocks/rclfluid");

    public FluidLiquidCrystal() {
        setRegistryName(new DeepResonanceResourceLocation("liquid_crystal"));
    }

    @Nonnull
    @Override
    protected FluidAttributes createAttributes() {
        return FluidAttributes.builder(texture, texture)
                .translationKey("fluid." + DeepResonance.MODID + "." + "liquid_crystal")
                .build(this);
    }

}
