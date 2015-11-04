package mcjty.deepresonance.blocks.laser;

import mcjty.lib.varia.Logging;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaserBonusConfiguration {
    public static final String CATEGORY_LASERBONUS = "laserbonus";

    private static String toString(InfusingBonus.Modifier modifier) {
        return modifier.getBonus() + ":" + modifier.getMaxOrMin();
    }

    private static InfusingBonus.Modifier toModifier(String m) {
        String[] split = StringUtils.split(m, ":");
        float bonus;
        float minmax;
        try {
            bonus = Float.parseFloat(split[0]);
            minmax = Float.parseFloat(split[1]);
        } catch (NumberFormatException e) {
            Logging.logError("Error parsing laserbonus config!");
            return InfusingBonus.Modifier.NONE;
        }
        return new InfusingBonus.Modifier(bonus, minmax);
    }

    public static void init(Configuration cfg) {
        ConfigCategory category = cfg.getCategory(CATEGORY_LASERBONUS);
        if (category.isEmpty()) {
            LaserTileEntity.createDefaultInfusionBonusMap();
            for (Map.Entry<String, InfusingBonus> entry : LaserTileEntity.infusingBonusMap.entrySet()) {
                InfusingBonus bonus = entry.getValue();
                cfg.get(CATEGORY_LASERBONUS, entry.getKey() + "_color", bonus.getColor(), "Beam color (1 = blue, 2 = red, 3 = green, 4 = yellow)");
                cfg.get(CATEGORY_LASERBONUS, entry.getKey() + "_purity", toString(bonus.getPurityModifier()), "A percentage:minmax purity bonus to add or subtract to the rcl infuse unit volume");
                cfg.get(CATEGORY_LASERBONUS, entry.getKey() + "_strength", toString(bonus.getStrengthModifier()), "A percentage:minmax strength bonus to add or subtract to the rcl infuse unit volume");
                cfg.get(CATEGORY_LASERBONUS, entry.getKey() + "_efficiency", toString(bonus.getEfficiencyModifier()), "A percentage:minmax efficiency bonus to add or subtract to the rcl infuse unit volume");
            }

        } else {
            LaserTileEntity.infusingBonusMap = new HashMap<String, InfusingBonus>();
            List<String> items = new ArrayList<String>();
            for (Map.Entry<String, Property> entry : category.entrySet()) {
                String key = entry.getKey();
                if (key.endsWith("_color")) {
                    String name = key.substring(0, key.indexOf("_color"));
                    System.out.println("name = " + name);
                    items.add(name);
                }
            }
            for (String name : items) {
                int color = cfg.get(CATEGORY_LASERBONUS, name + "_color", 1, "Beam color (1 = blue, 2 = red, 3 = green, 4 = yellow)").getInt();
                InfusingBonus.Modifier purityModifier = toModifier(cfg.get(CATEGORY_LASERBONUS, name + "_purity", "0:0", "A percentage:minmax purity bonus to add or subtract to the rcl infuse unit volume").getString());
                InfusingBonus.Modifier strengthModifier = toModifier(cfg.get(CATEGORY_LASERBONUS, name + "_strength", "0:0", "A percentage:minmax strength bonus to add or subtract to the rcl infuse unit volume").getString());
                InfusingBonus.Modifier efficiencyModifier = toModifier(cfg.get(CATEGORY_LASERBONUS, name + "_efficiency", "0:0", "A percentage:minmax efficiency bonus to add or subtract to the rcl infuse unit volume").getString());
                LaserTileEntity.infusingBonusMap.put(name, new InfusingBonus(color, purityModifier, strengthModifier, efficiencyModifier));
            }
        }
    }
}
