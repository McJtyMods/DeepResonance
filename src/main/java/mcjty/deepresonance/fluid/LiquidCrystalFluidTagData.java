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

    public static FluidStack makeLiquidCrystalStack(int amount, float quality, float purity, float power, float lastingTime, float crystallisingTime) {
        FluidStack stack = new FluidStack(DRFluidRegistry.liquidCrystal, amount);
        NBTTagCompound tagCompound = new NBTTagCompound();
        stack.tag = tagCompound;
        stack.tag.setFloat("quality", quality);
        stack.tag.setFloat("purity", purity);
        stack.tag.setFloat("power", power);
        stack.tag.setFloat("lastingTime", lastingTime);
        stack.tag.setFloat("crystallisingTime", crystallisingTime);
        return stack;
    }

    public static LiquidCrystalFluidTagData fromStack(FluidStack stack){
        if (!DRFluidRegistry.isValidLiquidCrystalStack(stack))
            return null;
        if (stack.tag == null)
            stack.tag = new NBTTagCompound();
        NBTTagCompound fluidTag = stack.tag;
        LiquidCrystalFluidTagData ret = fromNBT(fluidTag);
        ret.valid = stack.amount > 0;
        ret.referenceStack = stack;
        return ret;
    }

    public static LiquidCrystalFluidTagData fromNBT(NBTTagCompound fluidTag) {
        if (fluidTag == null) {
            return null;
        }
        LiquidCrystalFluidTagData ret = new LiquidCrystalFluidTagData();
        ret.valid = true;
        ret.quality = fluidTag.getFloat("quality");
        ret.purity = fluidTag.getFloat("purity");
        ret.power = fluidTag.getFloat("power");
        ret.lastingTime = fluidTag.getFloat("lastingTime");
        ret.crystallisingTime = fluidTag.getFloat("crystallisingTime");

        return ret;
    }

    private LiquidCrystalFluidTagData(){
    }

    private FluidStack referenceStack;
    private boolean valid;
    private float quality;
    private float purity;
    private float power;
    private float lastingTime;
    private float crystallisingTime;

    public void merge(LiquidCrystalFluidTagData otherTag){
        checkNullity();
        if (!otherTag.valid){
            return;
        }
        this.quality = calculate(otherTag, quality, otherTag.quality);
        this.purity = calculate(otherTag, purity, otherTag.purity);
        this.power = calculate(otherTag, power, otherTag.power);
        this.lastingTime = calculate(otherTag, lastingTime, otherTag.lastingTime);
        this.crystallisingTime = calculate(otherTag, lastingTime, otherTag.lastingTime);

        referenceStack.amount += otherTag.referenceStack.amount;
        save();
    }

    private float calculate(LiquidCrystalFluidTagData other, float myValue, float otherValue){
        float f = (other.getInternalTankAmount()/((float)getInternalTankAmount() + other.getInternalTankAmount()));
        return (1-f)*myValue + f*otherValue;
    }

    public void save(){
        writeDataToNBT(referenceStack.tag);
    }

    public void writeDataToNBT(NBTTagCompound tagCompound){
        checkNullity();
        tagCompound.setFloat("quality", quality);
        tagCompound.setFloat("purity", purity);
        tagCompound.setFloat("power", power);
        tagCompound.setFloat("lastingTime", lastingTime);
        tagCompound.setFloat("crystallisingTime", crystallisingTime);
    }

    private void checkNullity(){
        if (referenceStack == null || referenceStack.amount <= 0){
            valid = false;
            purity = 0;
        } else if (!valid){
            valid = true;
        }
    }

    /**
     * Getters
     */
    public float getQuality() {
        return quality;
    }

    public float getPurity() {
        return purity;
    }

    public float getPower() {
        return power;
    }

    public float getLastingTime() {
        return lastingTime;
    }

    public float getCrystallisingTime() {
        return crystallisingTime;
    }

    public int getInternalTankAmount() {
        return referenceStack.amount;
    }

    /**
     * Setters
     */
    public void setQuality(float quality) {
        this.quality = quality;
    }

    public void setPurity(float purity) {
        this.purity = purity;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public void setLastingTime(float lastingTime) {
        this.lastingTime = lastingTime;
    }

    public void setCrystallisingTime(float crystallisingTime) {
        this.crystallisingTime = crystallisingTime;
    }

    public void setInternalAmount(int i){
        referenceStack.amount = i;
    }

    public FluidStack getReferenceStack() {
        return referenceStack.copy();
    }

    @Override
    public String toString() {
        return "Amount: "+referenceStack.amount+" ,Quality: "+quality+" ,Purity: "+purity+" ,Power: "+power+" ,LastingTime: "+lastingTime+" ,CrystallisingTime: "+crystallisingTime;
    }
}
