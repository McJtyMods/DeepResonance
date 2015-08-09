package mcjty.deepresonance.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 9-8-2015.
 */
public final class LiquidCrystalFluidTagData {

    public static LiquidCrystalFluidTagData fromNBT(NBTTagCompound tag, int amount){
        return fromStack(new FluidStack(DRFluidRegistry.liquidCrystal, amount, tag));
    }

    public static LiquidCrystalFluidTagData fromStack(FluidStack stack){
        if (!DRFluidRegistry.isValidLiquidCrystalStack(stack))
            return null;
        if (stack.tag == null)
            stack.tag = new NBTTagCompound();
        NBTTagCompound fluidTag = stack.tag;
        LiquidCrystalFluidTagData ret = new LiquidCrystalFluidTagData();
        ret.purity = fluidTag.getFloat("purity");

        ret.referenceStack = stack;
        return ret;
    }

    private LiquidCrystalFluidTagData(){
    }

    private FluidStack referenceStack;
    private float purity;

    public void merge(LiquidCrystalFluidTagData otherTag){
        checkNullity();
        otherTag.checkNullity();
        float f = 1;
        if (otherTag.referenceStack.amount >= 0 && referenceStack.amount >= 0) {
            f = otherTag.referenceStack.amount / (float) referenceStack.amount;
        }
        purity = (purity+otherTag.purity*f)/2;

        referenceStack.amount += otherTag.referenceStack.amount;
        save();
    }

    public void save(){
        writeDataToNBT(referenceStack.tag);
    }

    public void writeDataToNBT(NBTTagCompound tagCompound){
        checkNullity();
        tagCompound.setFloat("purity", purity);
    }

    private void checkNullity(){
        if (referenceStack == null || referenceStack.amount <= 0){
            purity = 0;
        }
    }

    /**
     * Getters
     */
    public float getPurity() {
        return purity;
    }

    public int getInternalTankAmount() {
        return referenceStack.amount;
    }

    /**
     * Setters
     */
    public void setPurity(float purity) {
        this.purity = purity;
    }

    public void setInternalAmount(int i){
        referenceStack.amount = i;
    }

}
