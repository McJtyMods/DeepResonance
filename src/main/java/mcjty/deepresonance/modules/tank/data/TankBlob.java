package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.lib.multiblock.IMultiblock;
import net.minecraft.nbt.CompoundNBT;

public class TankBlob implements IMultiblock {
    private final int generatorBlocks;
    private int amount;
    private float quality;
    private float purity;
    private float strength;
    private float efficiency;

    private TankBlob(Builder builder) {
        this.generatorBlocks = builder.generatorBlocks;
        this.amount = builder.amount;
        this.quality = builder.quality;
        this.purity = builder.purity;
        this.strength = builder.strength;
        this.efficiency = builder.efficiency;
    }

    public int getGeneratorBlocks() {
        return generatorBlocks;
    }

    private float calculate(ILiquidCrystalData other, float myValue, float otherValue) {
        float f = (other.getAmount() / ((float) getAmount() + other.getAmount()));
        return (1 - f) * myValue + f * otherValue;
    }

    public int getAmount() {
        return amount;
    }

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }

    public float getPurity() {
        return purity;
    }

    public void setPurity(float purity) {
        this.purity = purity;
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public float getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(float efficiency) {
        this.efficiency = efficiency;
    }

    public static TankBlob load(CompoundNBT tagCompound) {
        return TankBlob.builder()
                .generatorBlocks(tagCompound.getInt("refcount"))
                .amount(tagCompound.getInt("amount"))
                .quality(tagCompound.getFloat("quality"))
                .purity(tagCompound.getFloat("purity"))
                .strength(tagCompound.getFloat("strength"))
                .efficiency(tagCompound.getFloat("efficiency"))
                .build();
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int generatorBlocks = 0;
        private int amount;
        private float quality;
        private float purity;
        private float strength;
        private float efficiency;

        public Builder network(TankBlob blob) {
            generatorBlocks = blob.generatorBlocks;
            amount = blob.amount;
            quality = blob.quality;
            purity = blob.purity;
            strength = blob.strength;
            efficiency = blob.efficiency;
            return this;
        }

        public Builder generatorBlocks(int generatorBlocks) {
            this.generatorBlocks = generatorBlocks;
            return this;
        }

        public Builder addGeneratorBlocks(int a) {
            this.generatorBlocks += a;
            return this;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder quality(float quality) {
            this.quality = quality;
            return this;
        }

        public Builder purity(float purity) {
            this.purity = purity;
            return this;
        }

        public Builder strength(float strength) {
            this.strength = strength;
            return this;
        }

        public Builder efficiency(float efficiency) {
            this.efficiency = efficiency;
            return this;
        }

        public TankBlob build() {
            return new TankBlob(this);
        }
    }
}
