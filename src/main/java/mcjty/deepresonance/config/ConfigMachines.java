package mcjty.deepresonance.config;

import elec332.core.config.Configurable;

/**
 * Created by Elec332 on 10-8-2015.
 */
public class ConfigMachines {

    public static class Smelter{
        private static final String category = "Smelter";

        @Configurable(category = category, minValue = 5, maxValue = 900)
        public static int rfPerTick = 10;

        @Configurable(category = category, minValue = 20, maxValue = 1000)
        public static int processTime = 400;

        @Configurable(category = category, minValue = 100, maxValue = 10000)
        public static int lavaCost = 200;

        @Configurable(category = category, minValue = 50, maxValue = 1000)
        public static int rclPerOre = 200;

    }
}
