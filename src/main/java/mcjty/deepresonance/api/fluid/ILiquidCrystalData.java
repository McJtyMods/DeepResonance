package mcjty.deepresonance.api.fluid;

import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 7-1-2020
 */
public interface ILiquidCrystalData {

    public void merge(ILiquidCrystalData otherStack);

    public float getQuality();

    public void setQuality(float quality);

    public float getPurity();

    public void setPurity(float purity);

    public float getStrength();

    public void setStrength(float strength);

    public float getEfficiency();

    public void setEfficiency(float efficiency);

    public int getAmount();

    public void setAmount(int i);

    public FluidStack getReferenceStack();

    default public FluidStack getReferenceStack(int amount) {
        FluidStack ret = getReferenceStack();
        ret.setAmount(amount);
        return ret;
    }

}
