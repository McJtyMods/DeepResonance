package mcjty.deepresonance.api.fluid;

import net.minecraftforge.fluids.FluidStack;

public interface ILiquidCrystalData {

    void merge(ILiquidCrystalData otherStack);

    float getQuality();

    void setQuality(double quality);

    float getPurity();

    void setPurity(double purity);

    float getStrength();

    void setStrength(double strength);

    float getEfficiency();

    void setEfficiency(double efficiency);

    int getAmount();

    void setAmount(int i);

    FluidStack toFluidStack();

    default FluidStack toFluidStack(int amount) {
        FluidStack ret = toFluidStack();
        ret.setAmount(amount);
        return ret;
    }

}
