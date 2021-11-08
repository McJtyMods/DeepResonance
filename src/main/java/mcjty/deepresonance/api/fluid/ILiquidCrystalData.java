package mcjty.deepresonance.api.fluid;

import net.minecraftforge.fluids.FluidStack;

public interface ILiquidCrystalData {

    void merge(ILiquidCrystalData otherStack);

    double getQuality();

    void setQuality(double quality);

    double getPurity();

    void setPurity(double purity);

    double getStrength();

    void setStrength(double strength);

    double getEfficiency();

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
