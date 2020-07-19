package mcjty.deepresonance.api.fluid;

import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 7-1-2020
 */
public interface ILiquidCrystalData {

    void merge(ILiquidCrystalData otherStack);

    float getQuality();

    void setQuality(float quality);

    float getPurity();

    void setPurity(float purity);

    float getStrength();

    void setStrength(float strength);

    float getEfficiency();

    void setEfficiency(float efficiency);

    int getAmount();

    void setAmount(int i);

    FluidStack getReferenceStack();

    default FluidStack getReferenceStack(int amount) {
        FluidStack ret = getReferenceStack();
        ret.setAmount(amount);
        return ret;
    }

}
