package mcjty.deepresonance.modules.machines.data;

import mcjty.lib.varia.Tools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public class InfusionBonusRegistry {

    private static Map<ResourceLocation, InfusingBonus> infusingBonusMap = null;

    public static final int COLOR_BLUE = 1;
    public static final int COLOR_RED = 2;
    public static final int COLOR_GREEN = 3;
    public static final int COLOR_YELLOW = 4;       // This is rendered as off in meta

    public InfusionBonusRegistry() {
        createDefaultInfusionBonusMap();
    }

    public static void createDefaultInfusionBonusMap() {
        infusingBonusMap = new HashMap<>();
        infusingBonusMap.put(Tools.getId(Items.DIAMOND), new InfusingBonus(
                COLOR_BLUE,
                new InfusingBonus.Modifier(5.0f, 100.0f),
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Tools.getId(Items.EMERALD), new InfusingBonus(
                COLOR_GREEN,
                new InfusingBonus.Modifier(8.0f, 100.0f),
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Tools.getId(Items.ENDER_PEARL), new InfusingBonus(
                COLOR_GREEN,
                new InfusingBonus.Modifier(2.0f, 100.0f),
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Tools.getId(Items.REDSTONE), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-1.0f, 0.0f),
                new InfusingBonus.Modifier(5.0f, 60.0f),
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Tools.getId(Items.GUNPOWDER), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-5.0f, 0.0f),
                new InfusingBonus.Modifier(8.0f, 70.0f),
                new InfusingBonus.Modifier(4.0f, 60.0f)));
        infusingBonusMap.put(Tools.getId(Items.GLOWSTONE_DUST), new InfusingBonus(
                COLOR_YELLOW,
                new InfusingBonus.Modifier(-2.0f, 0.0f),
                new InfusingBonus.Modifier(6.0f, 50.0f),
                new InfusingBonus.Modifier(3.0f, 50.0f)));
        infusingBonusMap.put(Tools.getId(Items.BLAZE_POWDER), new InfusingBonus(
                COLOR_YELLOW,
                new InfusingBonus.Modifier(-6.0f, 0.0f),
                new InfusingBonus.Modifier(5.0f, 70.0f),
                new InfusingBonus.Modifier(5.0f, 70.0f)));
        infusingBonusMap.put(Tools.getId(Items.QUARTZ), new InfusingBonus(
                COLOR_BLUE,
                new InfusingBonus.Modifier(-1.0f, 0.0f),
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(7.0f, 80.0f)));
        infusingBonusMap.put(Tools.getId(Items.NETHER_STAR), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-60.0f, 0.0f),
                new InfusingBonus.Modifier(90.0f, 100.0f),
                new InfusingBonus.Modifier(90.0f, 100.0f)));
        infusingBonusMap.put(Tools.getId(Items.GHAST_TEAR), new InfusingBonus(
                COLOR_YELLOW,
                new InfusingBonus.Modifier(-20.0f, 0.0f),
                new InfusingBonus.Modifier(25.0f, 100.0f),
                new InfusingBonus.Modifier(15.0f, 100.0f)));
        infusingBonusMap.put(Tools.getId(Items.PRISMARINE_SHARD), new InfusingBonus(
                COLOR_YELLOW,
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(3.0f, 30.0f),
                new InfusingBonus.Modifier(3.0f, 30.0f)));
        infusingBonusMap.put(Tools.getId(Items.PRISMARINE_CRYSTALS), new InfusingBonus(
                COLOR_YELLOW,
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(4.0f, 35.0f),
                new InfusingBonus.Modifier(4.0f, 35.0f)));
        infusingBonusMap.put(Tools.getId(Items.SLIME_BALL), new InfusingBonus(
                COLOR_GREEN,
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(-10.0f, 1.0f)));
        infusingBonusMap.put(Tools.getId(Items.COAL), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-1.0f, 0.0f),
                new InfusingBonus.Modifier(-10.0f, 0.0f),
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Tools.getId(Items.NETHER_WART), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-3.0f, 0.0f),
                new InfusingBonus.Modifier(2.0f, 35.0f),
                new InfusingBonus.Modifier(-2.0f, 1.0f)));
        infusingBonusMap.put(Tools.getId(Items.GOLD_INGOT), new InfusingBonus(
                COLOR_RED,
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(-1.0f, 0.0f),
                new InfusingBonus.Modifier(1.0f, 30.0f)));
        infusingBonusMap.put(Tools.getId(Items.IRON_INGOT), new InfusingBonus(
                COLOR_RED,
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(-2.0f, 0.0f),
                new InfusingBonus.Modifier(1.0f, 20.0f)));
        infusingBonusMap.put(Tools.getId(Items.SNOWBALL), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(1.0f, 30.0f),
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(1.0f, 40.0f)));
        infusingBonusMap.put(new ResourceLocation("rftoolsbase", "dimensionalshard"), new InfusingBonus(
                COLOR_BLUE,
                new InfusingBonus.Modifier(1.0f, 100.0f),
                new InfusingBonus.Modifier(8.0f, 80.0f),
                new InfusingBonus.Modifier(8.0f, 80.0f)));
    }

    public static Map<ResourceLocation, InfusingBonus> getInfusingBonusMap() {
        return infusingBonusMap;
    }

    public static InfusingBonus getInfusionBonus(ItemStack stack) {
        return infusingBonusMap
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equals(Tools.getId(stack)))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(InfusingBonus.EMPTY);
    }
}
