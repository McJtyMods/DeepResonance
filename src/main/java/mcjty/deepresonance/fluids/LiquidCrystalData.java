package mcjty.deepresonance.fluids;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.setup.FluidRegister;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 7-1-2020
 */
public class LiquidCrystalData implements ILiquidCrystalData {

    private final FluidStack referenceStack;
    private float quality;
    private float purity;
    private float strength;
    private float efficiency;

    private LiquidCrystalData(FluidStack referenceStack) {
        this.referenceStack = referenceStack;
    }

    public static LiquidCrystalData fromNBT(CompoundNBT tag, int amount) {
        return fromStack(new FluidStack(FluidRegister.liquidCrystal, amount, tag));
    }

    public static FluidStack makeLiquidCrystalStack(int amount, float quality, float purity, float strength, float efficiency) {
        LiquidCrystalData data = new LiquidCrystalData(new FluidStack(FluidRegister.liquidCrystal, amount));
        data.setAmount(amount);
        data.setQuality(quality);
        data.setPurity(purity);
        data.setStrength(strength);
        data.setEfficiency(efficiency);
        return data.toFluidStack();
    }

    public static LiquidCrystalData fromStack(FluidStack stack) {
        if (!FluidRegister.isValidLiquidCrystalStack(stack)) {
            return null;
        }
        CompoundNBT fluidTag = stack.getOrCreateTag();
        if (fluidTag == null) {
            return null;
        }
        LiquidCrystalData ret = new LiquidCrystalData(stack);
        ret.quality = fluidTag.getFloat("quality");
        ret.purity = fluidTag.getFloat("purity");
        ret.strength = fluidTag.getFloat("strength");
        ret.efficiency = fluidTag.getFloat("efficiency");

        return ret;
    }

    @Override
    public void merge(ILiquidCrystalData otherTag) {
        checkNullity();
        if (!isValid(otherTag)) {
            return;
        }
        this.quality = calculate(otherTag, getQuality(), otherTag.getQuality());
        this.purity = calculate(otherTag, getPurity(), otherTag.getPurity());
        this.strength = calculate(otherTag, getStrength(), otherTag.getStrength());
        this.efficiency = calculate(otherTag, getEfficiency(), otherTag.getEfficiency());

        referenceStack.setAmount(referenceStack.getAmount() + otherTag.getAmount());
        save();
    }

    private float calculate(ILiquidCrystalData other, float myValue, float otherValue) {
        float f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        return (1 - f) * myValue + f * otherValue;
    }

    private void save() {
        writeDataToNBT(referenceStack.getTag());
    }

    private void writeDataToNBT(CompoundNBT tagCompound) {
        checkNullity();
        tagCompound.putFloat("quality", quality);
        tagCompound.putFloat("purity", purity);
        tagCompound.putFloat("strength", strength);
        tagCompound.putFloat("efficiency", efficiency);
    }

    private void checkNullity() {
        isValid(this);
    }

    private boolean isValid(ILiquidCrystalData data) {
        if (data.getAmount() <= 0) {
            data.setPurity(0);
            return false;
        }
        return true;
    }

    @Override
    public float getQuality() {
        return quality;
    }

    @Override
    public void setQuality(double quality) {
        this.quality = (float) quality;
    }

    @Override
    public float getPurity() {
        return purity;
    }

    @Override
    public void setPurity(double purity) {
        this.purity = (float) purity;
    }

    @Override
    public float getStrength() {
        return strength;
    }

    @Override
    public void setStrength(double strength) {
        this.strength = (float) strength;
    }

    @Override
    public float getEfficiency() {
        return efficiency;
    }

    @Override
    public void setEfficiency(double efficiency) {
        this.efficiency = (float) efficiency;
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
        FluidStack stack = new FluidStack(FluidRegister.liquidCrystal, referenceStack.getAmount());
        writeDataToNBT(stack.getOrCreateTag());
        return stack;
    }

    @Override
    public String toString() {
        return "Amount: " + referenceStack.getAmount() + " ,Quality: " + quality + " ,Purity: " + purity + " ,Strength: " + strength + " ,Efficiency: " + efficiency;
    }

}
