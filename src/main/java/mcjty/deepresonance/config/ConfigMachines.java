package mcjty.deepresonance.config;

import elec332.core.config.Configurable;

/**
 * Created by Elec332 on 10-8-2015.
 */
public class ConfigMachines {

    public static class Smelter{
        private static final String category = "Smelter";

        @Configurable(category = category, minValue = 0, maxValue = 1000)
        public static int rfPerTick = 200;

        @Configurable(category = category, minValue = 0, maxValue = 1000)
        public static int rfPerOre = 10;

        @Configurable(category = category, minValue = 0, maxValue = 1000000000)
        public static int rfMaximum = 50000;

        @Configurable(category = category, minValue = 10, maxValue = 1000)
        public static int processTime = 400;

        @Configurable(category = category, minValue = 100, maxValue = 10000)
        public static int lavaCost = 200;

        @Configurable(category = category, minValue = 50, maxValue = 1000)
        public static int rclPerOre = 200;
    }

    public static class Crystalizer{
        private static final String category = "Crystalizer";

        @Configurable(category = category, minValue = 0, maxValue = 1000)
        public static int rfPerTick = 200;

        @Configurable(category = category, minValue = 0, maxValue = 1000)
        public static int rfPerRcl = 200;

        @Configurable(category = category, minValue = 0, maxValue = 1000000000)
        public static int rfMaximum = 50000;

        @Configurable(category = category, minValue = 10, maxValue = 1000)
        public static int processTime = 400;

        @Configurable(category = category, minValue = 10, maxValue = 100000)
        public static int rclPerCrystal = 5000;

        @Configurable(category = category, minValue = 1, maxValue = 100000)
        public static int rclPerTick = 10;
    }
}
