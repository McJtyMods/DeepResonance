package mcjty.deepresonance.modules.machines.data;

public record InfusingBonus(int color, Modifier purityModifier, Modifier strengthModifier,
                            Modifier efficiencyModifier) {

    public static final InfusingBonus EMPTY = new InfusingBonus(0, Modifier.NONE, Modifier.NONE, Modifier.NONE);

    public boolean isEmpty() {
        return Math.abs(purityModifier.bonus) < 0.001f && Math.abs(strengthModifier.bonus) < 0.001f && Math.abs(efficiencyModifier.bonus) < 0.001f;
    }

    public record Modifier(float bonus, float maxOrMin) {

        public static final Modifier NONE = new Modifier(0.0f, 0.0f);

        public float modify(double value, double quality, double factor) {
            return modify((float) value, (float) quality, (float) factor);
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
