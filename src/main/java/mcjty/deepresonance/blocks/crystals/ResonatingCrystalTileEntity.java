package mcjty.deepresonance.blocks.crystals;

import mcjty.entity.GenericTileEntity;
import net.minecraft.nbt.NBTTagCompound;

public class ResonatingCrystalTileEntity extends GenericTileEntity {

    // The total maximum RF you can get out of a crystal with the following characteristics:
    //    * S: Strength (0-100%)
    //    * P: Purity (0-100%)
    //    * E: Efficiency (0-100%)
    // Is equal to:
    //    * MaxRF = FullMax * (S/100) * ((P+20)/120)
    // The RF/tick you can get out of a crystal with the above characteristics is:
    //    * RFTick = FullRFTick * (E/100) * ((P+10)/110) + 1

    private float strength = 1.0f;
    private float power = 1.0f;         // Default 1% power
    private float efficiency = 1.0f;    // Default 1%
    private float purity = 1.0f;        // Default 1% purity

    private float powerPerTick = -1;    // Calculated value that contains the power/tick that is drained for this crystal.
    private int rfPerTick = -1;         // Calculated value that contains the RF/tick for this crystal.

    private boolean glowing = false;

    @Override
    public boolean canUpdate() {
        return false;
    }

    public float getStrength() {
        return strength;
    }

    public float getPower() {
        return power;
    }

    public float getEfficiency() {
        return efficiency;
    }

    public float getPurity() {
        return purity;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setStrength(float strength) {
        this.strength = strength;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setPower(float power) {
        this.power = power;
        markDirty();
        // Don't do block update. We query power on demand from the tooltip of the crystal.
//        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setEfficiency(float efficiency) {
        this.efficiency = efficiency;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setPurity(float purity) {
        this.purity = purity;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setGlowing(boolean glowing) {
        if (this.glowing == glowing) {
            return;
        }
        this.glowing = glowing;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public float getPowerPerTick() {
        if (powerPerTick < 0) {
            float totalRF = ResonatingCrystalConfiguration.maximumRF * strength / 100.0f + (purity + 20.0f) / 120.0f;
            powerPerTick = totalRF / getRfPerTick();
        }
        return powerPerTick;
    }

    public int getRfPerTick() {
        if (rfPerTick == -1) {
            rfPerTick = (int) (ResonatingCrystalConfiguration.maximumRFtick * efficiency / 100.0f + (purity + 10.0f) / 110.0f + 1);
        }
        return rfPerTick;
    }


    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        strength = tagCompound.getFloat("strength");
        power = tagCompound.getFloat("power");
        efficiency = tagCompound.getFloat("efficiency");
        purity = tagCompound.getFloat("purity");
        glowing = tagCompound.getBoolean("glowing");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setFloat("strength", strength);
        tagCompound.setFloat("power", power);
        tagCompound.setFloat("efficiency", efficiency);
        tagCompound.setFloat("purity", purity);
        tagCompound.setBoolean("glowing", glowing);
    }


}
