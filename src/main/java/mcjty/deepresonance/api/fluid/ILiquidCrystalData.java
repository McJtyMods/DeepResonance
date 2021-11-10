package mcjty.deepresonance.api.fluid;

import net.minecraftforge.fluids.FluidStack;

public interface ILiquidCrystalData {

    void merge(ILiquidCrystalData otherStack);

    void merge(FluidStack otherFluid);

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

    FluidStack getFluidStack();
}
