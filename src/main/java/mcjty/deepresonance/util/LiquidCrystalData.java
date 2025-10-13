package mcjty.deepresonance.util;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.setup.Registration;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

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
        setStats(referenceStack, quality, purity, strength, efficiency);
    }

    private static void setStats(FluidStack fluidStack, double quality, double purity, double strength, double efficiency) {
        LCD lcd = new LCD(quality, purity, strength, efficiency);
        fluidStack.set(Registration.ITEM_LCD_DATA, lcd);
    }

    private double mixQuality(FluidStack other) {
        double f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        double thisValue = referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).quality();
        double otherValue = other.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).quality();
        return (1 - f) * thisValue + f * otherValue;
    }

    private double mixPurity(FluidStack other) {
        double f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        double thisValue = referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).purity();
        double otherValue = other.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).purity();
        return (1 - f) * thisValue + f * otherValue;
    }

    private double mixStrength(FluidStack other) {
        double f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        double thisValue = referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).strength();
        double otherValue = other.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).strength();
        return (1 - f) * thisValue + f * otherValue;
    }

    private double mixEfficiency(FluidStack other) {
        double f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        double thisValue = referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).efficiency();
        double otherValue = other.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).efficiency();
        return (1 - f) * thisValue + f * otherValue;
    }

    @Override
    public double getQuality() {
        return referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).quality();
    }

    @Override
    public void setQuality(double quality) {
        LCD lcd = referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO);
        referenceStack.set(Registration.ITEM_LCD_DATA, lcd.withQuality(quality));
    }

    @Override
    public double getPurity() {
        return referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).purity();
    }

    @Override
    public void setPurity(double purity) {
        LCD lcd = referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO);
        referenceStack.set(Registration.ITEM_LCD_DATA, lcd.withPurity(purity));
    }

    @Override
    public double getStrength() {
        return referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).strength();
    }

    @Override
    public void setStrength(double strength) {
        LCD lcd = referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO);
        referenceStack.set(Registration.ITEM_LCD_DATA, lcd.withStrength(strength));
    }

    @Override
    public double getEfficiency() {
        return referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO).efficiency();
    }

    @Override
    public void setEfficiency(double efficiency) {
        LCD lcd = referenceStack.getOrDefault(Registration.ITEM_LCD_DATA, LCD.ZERO);
        referenceStack.set(Registration.ITEM_LCD_DATA, lcd.withEfficiency(efficiency));
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
