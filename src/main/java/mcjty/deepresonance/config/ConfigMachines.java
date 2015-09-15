package mcjty.deepresonance.config;

import elec332.core.config.Configurable;

/**
 * Created by Elec332 on 10-8-2015.
 */
public class ConfigMachines {

    public static class Purifier {
        private static final String category = "Purifier";

        @Configurable(category = category, minValue = 1, maxValue = 10000, comment = "Amount of ticks needed to purify one unit of RCL")
        public static int ticksPerPurify = 10;

        @Configurable(category = category, minValue = 1, maxValue = 10000, comment = "The amount of RCL we purify as one unit")
        public static int rclPerPurify = 10;
    }

    public static class Smelter {
        private static final String category = "Smelter";

        @Configurable(category = category, minValue = 0, maxValue = 1000, comment = "How much RF/t this machine can input from a generator/capacitor")
        public static int rfPerTick = 200;

        @Configurable(category = category, minValue = 0, maxValue = 1000, comment = "How much RF/t this machine consumes during smelting ores")
        public static int rfPerOre = 10;

        @Configurable(category = category, minValue = 0, maxValue = 1000000000, comment = "Maximum RF that can be stored in this machine")
        public static int rfMaximum = 50000;

        @Configurable(category = category, minValue = 10, maxValue = 1000, comment = "The number of ticks to smelt one ore")
        public static int processTime = 200;

        @Configurable(category = category, minValue = 100, maxValue = 10000, comment = "The amount of lava to smelt one ore")
        public static int lavaCost = 200;

        @Configurable(category = category, minValue = 50, maxValue = 1000, comment = "The amount of RCL to produce with one ore")
        public static int rclPerOre = 200;
    }

    public static class Crystalizer{
        private static final String category = "Crystalizer";

        @Configurable(category = category, minValue = 0, maxValue = 1000, comment = "How much RF/t this machine can input from a generator/capacitor")
        public static int rfPerTick = 200;

        @Configurable(category = category, minValue = 0, maxValue = 1000, comment = "How much RF this machine consumes for one crystalizing step")
        public static int rfPerRcl = 20;

        @Configurable(category = category, minValue = 0, maxValue = 1000000000, comment = "Maximum RF that can be stored in this machine")
        public static int rfMaximum = 50000;

        @Configurable(category = category, minValue = 10, maxValue = 100000, comment = "The amount of RCL that is needed for one crystal")
        public static int rclPerCrystal = 6000;

        @Configurable(category = category, minValue = 1, maxValue = 100000, comment = "The amount of RCL/t that is consumed during crystalizing")
        public static int rclPerTick = 1;
    }
}
