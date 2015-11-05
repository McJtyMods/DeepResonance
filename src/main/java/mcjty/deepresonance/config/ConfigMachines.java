package mcjty.deepresonance.config;

import elec332.core.config.Configurable;

/**
 * Created by Elec332 on 10-8-2015.
 */
public class ConfigMachines {

    public static class Purifier {
        private static final String category = "Purifier";

        @Configurable(category = category, minValue = 1, maxValue = 10000, comment = "Amount of ticks needed to purify one unit of RCL")
        public static int ticksPerPurify = 100;

        @Configurable(category = category, minValue = 1, maxValue = 10000, comment = "The amount of RCL we purify as one unit")
        public static int rclPerPurify = 200;

        @Configurable(category = category, minValue = 1, maxValue = 100, comment = "How much the purifier adds to the purity of a liquid (in %)")
        public static int addedPurity = 25;

        @Configurable(category = category, minValue = 1, maxValue = 100, comment = "Maximum purity that the purifier can handle (in %)")
        public static int maxPurity = 85;
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

    public static class Crystalizer {
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

    public static class Collector {
        public static final String category = "Collector";

        @Configurable(category = category, minValue = 1, maxValue = 16, comment = "Maximum horizontal distance to look for crystals")
        public static int maxHorizontalCrystalDistance = 10;

        @Configurable(category = category, minValue = 1, maxValue = 16, comment = "Maximum vertical distance to look for crystals")
        public static int maxVerticalCrystalDistance = 1;
    }

    public static class Valve {
        public static final String category = "Valve";

        @Configurable(category = category, minValue = 1, maxValue = 10000, comment = "Amount of ticks needed to transfer one unit of RCL")
        public static int ticksPerOperation = 5;

        @Configurable(category = category, minValue = 1, maxValue = 10000, comment = "The amount of RCL we process in one operation")
        public static int rclPerOperation = 100;
    }

    public static class Laser {
        public static final String category = "Laser";

        @Configurable(category = category, minValue = 0, maxValue = 50000, comment = "How much RF/t this machine can input from a generator/capacitor")
        public static int rfPerTick = 2000;

        @Configurable(category = category, minValue = 0, maxValue = 50000, comment = "How much RF this machine consumes for infusing one catalyst item")
        public static int rfUsePerCatalyst = 4000;

        @Configurable(category = category, minValue = 0, maxValue = 10000, comment = "How many multiples of 10 ticks are needed to infuse one catalyst item")
        public static int ticks10PerCatalyst = 4;

        @Configurable(category = category, minValue = 0, maxValue = 1000000000, comment = "Maximum RF that can be stored in this machine")
        public static int rfMaximum = 100000;

        @Configurable(category = category, minValue = 1, maxValue = 100000000, comment = "The maximum amount of liquified crystal this machine can hold (this is not RCL!)")
        public static int crystalLiquidMaximum = 10000;

        @Configurable(category = category, minValue = 1, maxValue = 10000000, comment = "The amount of liquified crystal one crystal will yield (this is not RCL!)")
        public static int crystalLiquidPerCrystal = 2000;

        @Configurable(category = category, minValue = 1, maxValue = 10000000, comment = "The amount of RCL we improve with one catalyst item")
        public static int rclPerCatalyst = 500;

        @Configurable(category = category, minValue = 1, maxValue = 10000000, comment = "The amount of crystal liquid we consume per catalyst item")
        public static int crystalLiquidPerCatalyst = 50;
    }
}
