package mcjty.deepresonance.util;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

/**
 * Support non RCL fluids as well. In that case the stats are not used
 */
public class LiquidCrystalData implements ILiquidCrystalData {

    private final FluidStack referenceStack;

    public static final LiquidCrystalData EMPTY = LiquidCrystalData.fromStack(FluidStack.EMPTY);

    private LiquidCrystalData(FluidStack referenceStack) {
        this.referenceStack = referenceStack.copy();
    }

    public static FluidStack makeLiquidCrystalStack(int amount, float quality, float purity, float strength, float efficiency) {
        FluidStack fluidStack = new FluidStack(CoreModule.LIQUID_CRYSTAL.get(), amount);
        setStats(fluidStack, quality, purity, strength, efficiency);
        return fluidStack;
    }

    public static FluidStack makeLiquidCrystalStack(int amount) {
        return makeLiquidCrystalStack(amount, 0, 0, 0, 0);
    }

    @Nonnull
    public static LiquidCrystalData fromStack(FluidStack stack) {
        return new LiquidCrystalData(stack);
    }

    public static boolean isLiquidCrystal(Fluid fluid) {
        return fluid == CoreModule.LIQUID_CRYSTAL.get();
    }

    public static boolean isValidLiquidCrystalStack(@Nonnull FluidStack stack) {
        return !stack.isEmpty() && isLiquidCrystal(stack.getRawFluid()); //Stack might have size 0
    }

    public boolean isEmpty() {
        return referenceStack.isEmpty();
    }

    @Override
    public void merge(ILiquidCrystalData other) {
        FluidStack otherFluid = other.getFluidStack();
        merge(otherFluid);
    }

    @Override
    public void merge(FluidStack otherFluid) {
        if (referenceStack.getFluid() != otherFluid.getFluid()) {
            return;
        }
        if (referenceStack.getFluid() == CoreModule.LIQUID_CRYSTAL.get() && otherFluid.getFluid() == CoreModule.LIQUID_CRYSTAL.get()) {
            double quality = mix(otherFluid, "quality");
            double purity = mix(otherFluid, "purity");
            double strength = mix(otherFluid, "strength");
            double efficiency = mix(otherFluid, "efficiency");

            referenceStack.setAmount(referenceStack.getAmount() + otherFluid.getAmount());
            setStats(quality, purity, strength, efficiency);
        } else {
            referenceStack.setAmount(referenceStack.getAmount() + otherFluid.getAmount());
        }
    }

    private void setStats(double quality, double purity, double strength, double efficiency) {
        setStats(referenceStack, quality, purity, strength, efficiency);
    }

    private static void setStats(FluidStack fluidStack, double quality, double purity, double strength, double efficiency) {
        CompoundTag tag = fluidStack.getOrCreateTag();
        tag.putDouble("quality", quality);
        tag.putDouble("purity", purity);
        tag.putDouble("strength", strength);
        tag.putDouble("efficiency", efficiency);
    }

    private double mix(FluidStack other, String tag) {
        double f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        double thisValue = referenceStack.getTag() == null ? 0 : referenceStack.getTag().getDouble(tag);
        double otherValue = other.getTag() == null ? 0 : other.getTag().getDouble(tag);
        return (1 - f) * thisValue + f * otherValue;
    }

    @Override
    public double getQuality() {
        CompoundTag tag = referenceStack.getTag();
        return tag == null ? 0 : tag.getDouble("quality");
    }

    @Override
    public void setQuality(double quality) {
        referenceStack.getOrCreateTag().putDouble("quality", quality);
    }

    @Override
    public double getPurity() {
        CompoundTag tag = referenceStack.getTag();
        return tag == null ? 0 : tag.getDouble("purity");
    }

    @Override
    public void setPurity(double purity) {
        referenceStack.getOrCreateTag().putDouble("purity", purity);
    }

    @Override
    public double getStrength() {
        CompoundTag tag = referenceStack.getTag();
        return tag == null ? 0 : tag.getDouble("strength");
    }

    @Override
    public void setStrength(double strength) {
        referenceStack.getOrCreateTag().putDouble("strength", strength);
    }

    @Override
    public double getEfficiency() {
        CompoundTag tag = referenceStack.getTag();
        return tag == null ? 0 : tag.getDouble("efficiency");
    }

    @Override
    public void setEfficiency(double efficiency) {
        referenceStack.getOrCreateTag().putDouble("efficiency", efficiency);
    }

    @Override
    public int getAmount() {
        return referenceStack.getAmount();
    }

    @Override
    public void setAmount(int i) {
        if (!referenceStack.isEmpty()) {
            referenceStack.setAmount(i);
        }
    }

    @Override
    public FluidStack getFluidStack() {
        return referenceStack;
    }

    @Override
    public String toString() {
        return "Amount: " + referenceStack.getAmount() + " ,Quality: " + getQuality() + " ,Purity: " + getPurity() + " ,Strength: " + getStrength() + " ,Efficiency: " + getEfficiency();
    }

}
