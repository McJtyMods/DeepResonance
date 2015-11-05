package mcjty.deepresonance.blocks.laser;

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

        public float modify(float value, float quality) {
            if (bonus == 0.0f) {
                return value;
            }
            value += bonus / 100.0f;
            if (bonus > 0) {
                if (value > maxOrMin*quality) {
                    value = maxOrMin*quality;
                }
            } else {
                if (value < maxOrMin*quality) {
                    value = maxOrMin*quality;
                }
            }
            return value;
        }
    }
}
