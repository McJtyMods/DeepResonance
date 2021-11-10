package mcjty.deepresonance.util;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

/**
 * Support non RCL fluids as well. In that case the stats are not used
 */
public class LiquidCrystalData implements ILiquidCrystalData {

    private final FluidStack referenceStack;

    private LiquidCrystalData(FluidStack referenceStack) {
        this.referenceStack = referenceStack.copy();
    }

    public LiquidCrystalData(PacketBuffer buf) {
        this.referenceStack = buf.readFluidStack();
    }

    static FluidStack makeLiquidCrystalStack(int amount, float quality, float purity, float strength, float efficiency) {
        LiquidCrystalData data = new LiquidCrystalData(new FluidStack(CoreModule.LIQUID_CRYSTAL.get(), amount));
        data.setAmount(amount);
        data.setStats(quality, purity, strength, efficiency);
        return data.getFluidStack();
    }

    @Nonnull
    public static LiquidCrystalData fromStack(FluidStack stack) {
        return new LiquidCrystalData(stack);
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
        CompoundNBT tag = referenceStack.getOrCreateTag();
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

    public void toBytes(PacketBuffer buf) {
        buf.writeFluidStack(referenceStack);
    }

    @Override
    public double getQuality() {
        CompoundNBT tag = referenceStack.getTag();
        return tag == null ? 0 : tag.getDouble("quality");
    }

    @Override
    public void setQuality(double quality) {
        referenceStack.getOrCreateTag().putDouble("quality", quality);
    }

    @Override
    public double getPurity() {
        CompoundNBT tag = referenceStack.getTag();
        return tag == null ? 0 : tag.getDouble("purity");
    }

    @Override
    public void setPurity(double purity) {
        referenceStack.getOrCreateTag().putDouble("purity", purity);
    }

    @Override
    public double getStrength() {
        CompoundNBT tag = referenceStack.getTag();
        return tag == null ? 0 : tag.getDouble("strength");
    }

    @Override
    public void setStrength(double strength) {
        referenceStack.getOrCreateTag().putDouble("strength", strength);
    }

    @Override
    public double getEfficiency() {
        CompoundNBT tag = referenceStack.getTag();
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
        referenceStack.setAmount(i);
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
