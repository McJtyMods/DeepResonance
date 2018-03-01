package mcjty.deepresonance.config;

import mcjty.deepresonance.DeepResonance;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;

/**
 * Created by Elec332 on 10-8-2015.
 */
@Config(modid = DeepResonance.MODID, name = DeepResonance.MODID + "/machines", category = "")
public class ConfigMachines {

    public static General general = new General();
    public static class General {
        @Comment("Experimental fix for weird behaviour of machines that interact with DR tanks.")
        public boolean experimentalChunkBorderFix = true;
    }

    public static PlateBlock plateBlock = new PlateBlock();
    public static class PlateBlock {
        @RangeInt(min = 0, max = 100000)
        @Comment("Strength of radiation that a plate block gives when it has a redstone signal. 0 to disable")
        public int radiationStrength = 20000;

        @RangeInt(min = 0, max = 100000)
        @Comment("Radius of radiation that a plate block gives when it has a redstone signal")
        public int radiationRadius = 10;

        @RangeInt(min = 0, max = 100000)
        @Comment("Amount of ticks that the radiation lasts from a plate block")
        public int radiationTicks = 100;
    }

    public static Pulser pulser = new Pulser();
    public static class Pulser {
        @RangeInt(min = 0, max = 1000)
        @Comment("How much RF/t this machine can input from a generator/capacitor")
        public int rfPerTick = 100;

        @RangeInt(min = 0, max = 1000000000)
        @Comment("Maximum RF that can be stored in this machine")
        public int rfMaximum = 100;

        @RangeInt(min = 1, max = 100000)
        @Comment("The RF needed for a single pulse (should be a multiple of 15!)")
        public int rfPerPulse = 60;
    }

    public static Purifier purifier = new Purifier();
    public static class Purifier {
        @RangeInt(min = 1, max = 10000)
        @Comment("Amount of ticks needed to purify one unit of RCL")
        public int ticksPerPurify = 100;

        @RangeInt(min = 1, max = 10000)
        @Comment("The amount of RCL we purify as one unit")
        public int rclPerPurify = 200;

        @RangeInt(min = 1, max = 100)
        @Comment("How much the purifier adds to the purity of a liquid (in %)")
        public int addedPurity = 25;

        @RangeInt(min = 1, max = 100)
        @Comment("Maximum purity that the purifier can handle (in %)")
        public int maxPurity = 85;
    }

    public static Smelter smelter = new Smelter();
    public static class Smelter {
        @RangeInt(min = 0, max = 1000)
        @Comment("How much RF/t this machine can input from a generator/capacitor")
        public int rfPerTick = 200;

        @RangeInt(min = 0, max = 1000)
        @Comment("How much RF/t this machine consumes during smelting ores")
        public int rfPerOre = 10;

        @RangeInt(min = 0, max = 1000000000)
        @Comment("Maximum RF that can be stored in this machine")
        public int rfMaximum = 50000;

        @RangeInt(min = 10, max = 1000)
        @Comment("The number of ticks to smelt one ore")
        public int processTime = 200;

        @RangeInt(min = 100, max = 10000)
        @Comment("The amount of lava to smelt one ore")
        public int lavaCost = 200;

        @RangeInt(min = 50, max = 1000)
        @Comment("The amount of RCL to produce with one ore")
        public int rclPerOre = 200;
    }

    public static Crystalizer crystalizer = new Crystalizer();
    public static class Crystalizer {
        @RangeInt(min = 0, max = 1000)
        @Comment("How much RF/t this machine can input from a generator/capacitor")
        public int rfPerTick = 200;

        @RangeInt(min = 0, max = 1000)
        @Comment("How much RF this machine consumes for one crystalizing step")
        public int rfPerRcl = 20;

        @RangeInt(min = 0, max = 1000000000)
        @Comment("Maximum RF that can be stored in this machine")
        public int rfMaximum = 50000;

        @RangeInt(min = 10, max = 100000)
        @Comment("The amount of RCL that is needed for one crystal")
        public int rclPerCrystal = 6000;

        @RangeInt(min = 1, max = 100000)
        @Comment("The amount of RCL/t that is consumed during crystalizing")
        public int rclPerTick = 1;
    }

    public static Collector collector = new Collector();
    public static class Collector {
        @RangeInt(min = 1, max = 16)
        @Comment("Maximum horizontal distance to look for crystals")
        public int maxHorizontalCrystalDistance = 10;

        @RangeInt(min = 1, max = 16)
        @Comment("Maximum vertical distance to look for crystals")
        public int maxVerticalCrystalDistance = 1;
    }

    public static Valve valve = new Valve();
    public static class Valve {
        @RangeInt(min = 1, max = 10000)
        @Comment("Amount of ticks needed to transfer one unit of RCL")
        public int ticksPerOperation = 5;

        @RangeInt(min = 1, max = 10000)
        @Comment("The amount of RCL we process in one operation")
        public int rclPerOperation = 100;
    }

    public static Laser laser = new Laser();
    public static class Laser {
        @RangeInt(min = 0, max = 50000)
        @Comment("How much RF/t this machine can input from a generator/capacitor")
        public int rfPerTick = 2000;

        @RangeInt(min = 0, max = 50000)
        @Comment("How much RF this machine consumes for infusing one catalyst item")
        public int rfUsePerCatalyst = 4000;

        @RangeInt(min = 0, max = 10000)
        @Comment("How many multiples of 10 ticks are needed to infuse one catalyst item")
        public int ticks10PerCatalyst = 4;

        @RangeInt(min = 0, max = 1000000000)
        @Comment("Maximum RF that can be stored in this machine")
        public int rfMaximum = 100000;

        @RangeInt(min = 1, max = 100000000)
        @Comment("The maximum amount of liquified crystal this machine can hold (this is not RCL!)")
        public int crystalLiquidMaximum = 20000;

        @RangeInt(min = 1, max = 10000000)
        @Comment("The minimum amount of liquified crystal one crystal will yield (this is not RCL!). This value is for a 0% strength crystal")
        public int minCrystalLiquidPerCrystal = 2000;

        @RangeInt(min = 1, max = 10000000)
        @Comment("The maximum amount of liquified crystal one crystal will yield (this is not RCL!). This value is for a 100% strength crystal")
        public int maxCrystalLiquidPerCrystal = 10000;

        @RangeInt(min = 1, max = 10000000)
        @Comment("The amount of RCL we improve with one catalyst item")
        public int rclPerCatalyst = 500;

        @RangeInt(min = 1, max = 10000000)
        @Comment("The amount of crystal liquid we consume per catalyst item")
        public int crystalLiquidPerCatalyst = 25;
    }

    public static Power power = new Power();
    public static class Power {
        @RangeInt(min = 1, max = 2000000000)
        @Comment("The maximum kilo-RF (per 1000, so 1000 = 1milion RF) that a crystal with 100% power can hold")
        public int maximumKiloRF = 1000000;

        @RangeInt(min = 0, max = 20000)
        @Comment("The maximum RF/tick that a crystal with 100% efficiency can give")
        public int maximumRFPerTick = 20000;

    }
}
