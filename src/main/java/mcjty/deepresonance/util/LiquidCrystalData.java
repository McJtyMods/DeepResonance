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
        this.referenceStack = referenceStack;
    }

    public LiquidCrystalData(PacketBuffer buf) {
        this.referenceStack = buf.readFluidStack();
    }

    static FluidStack makeLiquidCrystalStack(int amount, float quality, float purity, float strength, float efficiency) {
        LiquidCrystalData data = new LiquidCrystalData(new FluidStack(CoreModule.LIQUID_CRYSTAL.get(), amount));
        data.setAmount(amount);
        data.setStats(quality, purity, strength, efficiency);
        return data.toFluidStack();
    }

    @Nonnull
    public static LiquidCrystalData fromStack(FluidStack stack) {
        return new LiquidCrystalData(stack);
    }

    @Override
    public void merge(ILiquidCrystalData otherTag) {
        if (referenceStack.getFluid() != otherTag.toFluidStack().getFluid()) {
            return; // Can't merge
        }
        if (referenceStack.getFluid() == CoreModule.LIQUID_CRYSTAL.get() && otherTag.toFluidStack().getFluid() == CoreModule.LIQUID_CRYSTAL.get()) {
            double quality = mix(otherTag, getQuality(), otherTag.getQuality());
            double purity = mix(otherTag, getPurity(), otherTag.getPurity());
            double strength = mix(otherTag, getStrength(), otherTag.getStrength());
            double efficiency = mix(otherTag, getEfficiency(), otherTag.getEfficiency());

            referenceStack.setAmount(referenceStack.getAmount() + otherTag.getAmount());
            setStats(quality, purity, strength, efficiency);
        } else {
            referenceStack.setAmount(referenceStack.getAmount() + otherTag.getAmount());
        }
    }

    private void setStats(double quality, double purity, double strength, double efficiency) {
        CompoundNBT tag = referenceStack.getOrCreateTag();
        tag.putDouble("quality", quality);
        tag.putDouble("purity", purity);
        tag.putDouble("strength", strength);
        tag.putDouble("efficiency", efficiency);
    }

    private double mix(ILiquidCrystalData other, double myValue, double otherValue) {
        double f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        return (1 - f) * myValue + f * otherValue;
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
    public FluidStack toFluidStack() {
        return referenceStack;
    }

    @Override
    public String toString() {
        return "Amount: " + referenceStack.getAmount() + " ,Quality: " + getQuality() + " ,Purity: " + getPurity() + " ,Strength: " + getStrength() + " ,Efficiency: " + getEfficiency();
    }

}
