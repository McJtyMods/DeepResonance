package mcjty.deepresonance.api.infusion;

import java.io.Serializable;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public final class InfusionModifier implements Serializable {

    public static final InfusionModifier NONE = new InfusionModifier(0.0f, 0.0f);

    private final float bonus;      // Can be positive or negative. A number between 0 and 100
    private final float maxOrMin;   // Max if bonus is positive, otherwise minimum

    public InfusionModifier(float bonus, float maxOrMin) {
        this.maxOrMin = maxOrMin;
        this.bonus = bonus;
    }

    public float getMaxOrMin() {
        return maxOrMin;
    }

    public float getBonus() {
        return bonus;
    }

    public void applyModifier(DoubleSupplier getter, DoubleConsumer setter, double quality, double efficiency) {
        if (bonus == 0) {
            return;
        }
        double value = getter.getAsDouble();
        double toAdd = (bonus / 100.0f) * efficiency;
        double cap = (maxOrMin / 100.0f) * quality;
        if (bonus > 0) {
            if (value + toAdd > cap) {
                toAdd = cap - value;
                if (toAdd < 0) {
                    toAdd = 0;
                }
            }
        } else {
            if (value + toAdd < cap) {
                toAdd = cap - value;
                if (toAdd > 0) {
                    toAdd = 0;
                }
            }
        }
        setter.accept(value + toAdd);
    }

}
