package mcjty.deepresonance.api.infusion;

import net.minecraft.item.DyeColor;

import java.io.Serializable;

public final class InfusionBonus implements Serializable {

    public static final InfusionBonus EMPTY = new InfusionBonus(0, 30, 0, 0, 0, InfusionModifier.NONE, InfusionModifier.NONE, InfusionModifier.NONE);

    private final int color;
    private final int duration;
    private final int powerPerTick;
    private final int crystalLiquidCostPerCatalyst;
    private final int rclImprovedPerCatalyst;
    private final InfusionModifier purityModifier;
    private final InfusionModifier strengthModifier;
    private final InfusionModifier efficiencyModifier;

    public InfusionBonus(DyeColor color, InfusionModifier purityModifier, InfusionModifier strengthModifier, InfusionModifier efficiencyModifier) {
        this(color.getColorValue(), 40, 100, 25, 500, purityModifier, strengthModifier, efficiencyModifier);
    }

    public InfusionBonus(DyeColor color, int duration, int powerPerTick, InfusionModifier purityModifier, InfusionModifier strengthModifier, InfusionModifier efficiencyModifier, int crystalLiquidPerCatalyst, int rclPerCatalyst) {
        this(color.getColorValue(), duration, powerPerTick, crystalLiquidPerCatalyst, rclPerCatalyst, purityModifier, strengthModifier, efficiencyModifier);
    }

    public InfusionBonus(int color, int duration, int powerPerTick, int crystalLiquidPerCatalyst, int rclPerCatalyst, InfusionModifier purityModifier, InfusionModifier strengthModifier, InfusionModifier efficiencyModifier) {
        this.color = color;
        this.duration = duration;
        this.powerPerTick = powerPerTick;
        this.crystalLiquidCostPerCatalyst = crystalLiquidPerCatalyst;
        this.rclImprovedPerCatalyst = rclPerCatalyst;
        this.purityModifier = purityModifier;
        this.strengthModifier = strengthModifier;
        this.efficiencyModifier = efficiencyModifier;
    }

    public int getColor() {
        return color;
    }

    public int getDuration() {
        return duration;
    }

    public int getPowerPerTick() {
        return powerPerTick;
    }

    public int getCrystalLiquidCostPerCatalyst() {
        return crystalLiquidCostPerCatalyst;
    }

    public float getCrystalLiquidCostPerTick() {
        return getCrystalLiquidCostPerCatalyst() / (float) getDuration();
    }

    public int getRclImprovedPerCatalyst() {
        return rclImprovedPerCatalyst;
    }

    public InfusionModifier getEfficiencyModifier() {
        return efficiencyModifier;
    }

    public InfusionModifier getPurityModifier() {
        return purityModifier;
    }

    public InfusionModifier getStrengthModifier() {
        return strengthModifier;
    }

    public boolean isEmpty() {
        return color == 0 && powerPerTick == 0 && rclImprovedPerCatalyst == 0;
    }

}
