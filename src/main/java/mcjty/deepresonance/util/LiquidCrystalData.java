package mcjty.deepresonance.util;

import net.minecraft.core.component.DataComponentPatch;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.data.LCD;
import net.minecraft.world.level.material.Fluid;
import mcjty.lib.varia.FluidStack;

import javax.annotation.Nonnull;

/**
 * Support non RCL fluids as well. In that case the stats are not used
 */
public class LiquidCrystalData implements ILiquidCrystalData {

    private FluidStack referenceStack;

    public static final LiquidCrystalData EMPTY = LiquidCrystalData.fromStack(FluidStack.EMPTY);

    private LiquidCrystalData(FluidStack referenceStack) {
        this.referenceStack = referenceStack.copy();
    }

    public static FluidStack makeLiquidCrystalStack(int amount, float quality, float purity, float strength, float efficiency) {
        return makeLiquidCrystalStack(amount, new LCD(quality, purity, strength, efficiency));
    }

    public static FluidStack makeLiquidCrystalStack(int amount, LCD lcd) {
        var components = DataComponentPatch.builder().set(CoreModule.ITEM_LCD_DATA.get(), lcd).build();
        return new FluidStack(FluidVariant.of(CoreModule.LIQUID_CRYSTAL.get(), components), amount);
    }

    public static FluidStack makeLiquidCrystalStack(int amount) {
        return makeLiquidCrystalStack(amount, 0, 0, 0, 0);
    }

    @Nonnull
    public static LiquidCrystalData fromStack(FluidStack stack) {
        return new LiquidCrystalData(stack);
    }

    public static FluidStack readStack(HolderLookup.Provider lookup, CompoundTag tag) {
        return FluidStack.OPTIONAL_CODEC.parse(lookup.createSerializationContext(NbtOps.INSTANCE), tag)
                .result().orElse(FluidStack.EMPTY);
    }

    public static CompoundTag writeStack(FluidStack stack, HolderLookup.Provider lookup) {
        return (CompoundTag) FluidStack.OPTIONAL_CODEC.encodeStart(lookup.createSerializationContext(NbtOps.INSTANCE), stack).getOrThrow();
    }

    public static boolean isLiquidCrystal(Fluid fluid) {
        return fluid == CoreModule.LIQUID_CRYSTAL.get();
    }

    public static boolean isValidLiquidCrystalStack(@Nonnull FluidStack stack) {
        return !stack.isEmpty() && isLiquidCrystal(stack.getFluid()); //Stack might have size 0
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
            double quality = mixQuality(otherFluid);
            double purity = mixPurity(otherFluid);
            double strength = mixStrength(otherFluid);
            double efficiency = mixEfficiency(otherFluid);

            referenceStack.setAmount(referenceStack.getAmount() + otherFluid.getAmount());
            setStats(quality, purity, strength, efficiency);
        } else {
            referenceStack.setAmount(referenceStack.getAmount() + otherFluid.getAmount());
        }
    }

    private void setStats(double quality, double purity, double strength, double efficiency) {
        referenceStack = makeLiquidCrystalStack(getAmount(), new LCD(quality, purity, strength, efficiency));
    }

    private double mixQuality(FluidStack other) {
        double f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        double thisValue = getLCD().quality();
        double otherValue = other.variant().getComponents().getOrDefault(CoreModule.ITEM_LCD_DATA.get(), LCD.ZERO).quality();
        return (1 - f) * thisValue + f * otherValue;
    }

    private double mixPurity(FluidStack other) {
        double f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        double thisValue = getLCD().purity();
        double otherValue = other.variant().getComponents().getOrDefault(CoreModule.ITEM_LCD_DATA.get(), LCD.ZERO).purity();
        return (1 - f) * thisValue + f * otherValue;
    }

    private double mixStrength(FluidStack other) {
        double f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        double thisValue = getLCD().strength();
        double otherValue = other.variant().getComponents().getOrDefault(CoreModule.ITEM_LCD_DATA.get(), LCD.ZERO).strength();
        return (1 - f) * thisValue + f * otherValue;
    }

    private double mixEfficiency(FluidStack other) {
        double f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        double thisValue = getLCD().efficiency();
        double otherValue = other.variant().getComponents().getOrDefault(CoreModule.ITEM_LCD_DATA.get(), LCD.ZERO).efficiency();
        return (1 - f) * thisValue + f * otherValue;
    }

    @Override
    public double getQuality() {
        return getLCD().quality();
    }

    @Override
    public void setQuality(double quality) {
        LCD lcd = getLCD();
        referenceStack = makeLiquidCrystalStack(getAmount(), lcd.withQuality(quality));
    }

    @Override
    public double getPurity() {
        return getLCD().purity();
    }

    @Override
    public void setPurity(double purity) {
        LCD lcd = getLCD();
        referenceStack = makeLiquidCrystalStack(getAmount(), lcd.withPurity(purity));
    }

    @Override
    public double getStrength() {
        return getLCD().strength();
    }

    @Override
    public void setStrength(double strength) {
        LCD lcd = getLCD();
        referenceStack = makeLiquidCrystalStack(getAmount(), lcd.withStrength(strength));
    }

    @Override
    public double getEfficiency() {
        return getLCD().efficiency();
    }

    @Override
    public void setEfficiency(double efficiency) {
        LCD lcd = getLCD();
        referenceStack = makeLiquidCrystalStack(getAmount(), lcd.withEfficiency(efficiency));
    }

    @Override
    public int getAmount() {
        return referenceStack.getAmount();
    }

    public LCD getLCD() {
        return referenceStack.variant().getComponents().getOrDefault(CoreModule.ITEM_LCD_DATA.get(), LCD.ZERO);
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
