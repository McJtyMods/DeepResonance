package mcjty.deepresonance.modules.machines.data;

public class InfusingBonus {
    private final int color;
    private final Modifier purityModifier;
    private final Modifier strengthModifier;
    private final Modifier efficiencyModifier;

    public InfusingBonus(int color, Modifier purityModifier, Modifier strengthModifier, Modifier efficiencyModifier) {
        this.color = color;
        this.efficiencyModifier = efficiencyModifier;
        this.purityModifier = purityModifier;
        this.strengthModifier = strengthModifier;
    }

    public int getColor() {
        return color;
    }

    public Modifier getEfficiencyModifier() {
        return efficiencyModifier;
    }

    public Modifier getPurityModifier() {
        return purityModifier;
    }

    public Modifier getStrengthModifier() {
        return strengthModifier;
    }

    public static class Modifier {
        private final float bonus;      // Can be positive or negative. A number between 0 and 100
        private final float maxOrMin;   // Max if bonus is positive, otherwise minimum

        public static final Modifier NONE = new Modifier(0.0f, 0.0f);

        public Modifier(float bonus, float maxOrMin) {
            this.maxOrMin = maxOrMin;
            this.bonus = bonus;
        }

        public float getMaxOrMin() {
            return maxOrMin;
        }

        public float getBonus() {
            return bonus;
        }

        public float modify(float value, float quality, float factor) {
            if (bonus == 0.0f) {
                return value;
            }
            float toAdd = factor * bonus / 100.0f;
            float cap = maxOrMin / 100.0f * quality;

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
            value += toAdd;
            return value;
        }
    }
}
