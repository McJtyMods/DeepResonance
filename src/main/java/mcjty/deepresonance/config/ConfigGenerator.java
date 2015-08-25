package mcjty.deepresonance.config;

import elec332.core.config.Configurable;

/**
 * Created by Elec332 on 25-8-2015.
 */
public class ConfigGenerator {

    public static class Crystal{

        public static final String category = "crystals";

        @Configurable(category = category, minValue = 0, maxValue = 500000000, comment = "The maximum RF that a crystal with 100% power can hold")
        public static int maximumRF = 500000000;

        @Configurable(category = category, minValue = 0, maxValue = 20000, comment = "The maximum RF/tick that a crystal with 100% efficiency can give")
        public static int maximumRFPerTick = 20000;

    }
}
