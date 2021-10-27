package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.lib.multiblock.IMultiblock;
import net.minecraft.nbt.CompoundNBT;

public class TankBlob implements IMultiblock {
    private int generatorBlocks;
    private int amount;
    private float quality;
    private float purity;
    private float strength;
    private float efficiency;

    public TankBlob() {
    }

    public TankBlob(TankBlob other) {
        this.generatorBlocks = other.generatorBlocks;
        this.amount = other.amount;
        this.quality = other.quality;
        this.purity = other.purity;
        this.strength = other.strength;
        this.efficiency = other.efficiency;
    }

    private float calculate(ILiquidCrystalData other, float myValue, float otherValue) {
        float f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        return (1 - f) * myValue + f * otherValue;
    }

    public TankBlob setGeneratorBlocks(int generatorBlocks) {
        this.generatorBlocks = generatorBlocks;
        return this;
    }

    public int getGeneratorBlocks() {
        return generatorBlocks;
    }

    public TankBlob setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public float getQuality() {
        return quality;
    }

    public TankBlob setQuality(float quality) {
        this.quality = quality;
        return this;
    }

    public float getPurity() {
        return purity;
    }

    public TankBlob setPurity(float purity) {
        this.purity = purity;
        return this;
    }

    public float getStrength() {
        return strength;
    }

    public TankBlob setStrength(float strength) {
        this.strength = strength;
        return this;
    }

    public float getEfficiency() {
        return efficiency;
    }

    public TankBlob setEfficiency(float efficiency) {
        this.efficiency = efficiency;
        return this;
    }

    public static TankBlob load(CompoundNBT tagCompound) {
        return new TankBlob()
                .setGeneratorBlocks(tagCompound.getInt("refcount"))
                .setAmount(tagCompound.getInt("amount"))
                .setQuality(tagCompound.getFloat("quality"))
                .setPurity(tagCompound.getFloat("purity"))
                .setStrength(tagCompound.getFloat("strength"))
                .setEfficiency(tagCompound.getFloat("efficiency"));
    }

    public static CompoundNBT save(CompoundNBT tagCompound, TankBlob network) {
        tagCompound.putInt("refcount", network.generatorBlocks);
        tagCompound.putInt("amount", network.amount);
        tagCompound.putFloat("quality", network.quality);
        tagCompound.putFloat("purity", network.purity);
        tagCompound.putFloat("strength", network.strength);
        tagCompound.putFloat("efficiency", network.efficiency);
        return tagCompound;
    }
}
