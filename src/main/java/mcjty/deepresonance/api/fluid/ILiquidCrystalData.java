package mcjty.deepresonance.api.fluid;

import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 7-1-2020
 */
public interface ILiquidCrystalData {

    public void merge(ILiquidCrystalData otherStack);

    public float getQuality();

    public float getPurity();

    public float getStrength();

    public float getEfficiency();

    public int getAmount();

    public void setQuality(float quality);

    public void setPurity(float purity);

    public void setStrength(float strength);

    public void setEfficiency(float efficiency);

    public void setAmount(int i);

    public FluidStack getReferenceStack();

    default public FluidStack getReferenceStack(int amount) {
        FluidStack ret = getReferenceStack();
        ret.setAmount(amount);
        return ret;
    }

}
