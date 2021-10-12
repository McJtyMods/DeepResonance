package mcjty.deepresonance.modules.machines.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.infusion.InfusionBonus;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class InfusionBonusRegistry {

    private final Map<Ingredient, InfusionBonus> bonuses;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static Map<ResourceLocation, InfusingBonus> infusingBonusMap = null;

    public static final int COLOR_BLUE = 1;
    public static final int COLOR_RED = 2;
    public static final int COLOR_GREEN = 3;
    public static final int COLOR_YELLOW = 4;       // This is rendered as off in meta

    public InfusionBonusRegistry() {
        this.bonuses = Maps.newHashMap();
    }

    public static void createDefaultInfusionBonusMap() {
        infusingBonusMap = new HashMap<ResourceLocation, InfusingBonus>();
        infusingBonusMap.put(Items.DIAMOND.getRegistryName(), new InfusingBonus(
                COLOR_BLUE,
                new InfusingBonus.Modifier(5.0f, 100.0f),
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Items.EMERALD.getRegistryName(), new InfusingBonus(
                COLOR_GREEN,
                new InfusingBonus.Modifier(8.0f, 100.0f),
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Items.ENDER_PEARL.getRegistryName(), new InfusingBonus(
                COLOR_GREEN,
                new InfusingBonus.Modifier(2.0f, 100.0f),
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Items.REDSTONE.getRegistryName(), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-1.0f, 0.0f),
                new InfusingBonus.Modifier(5.0f, 60.0f),
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Items.GUNPOWDER.getRegistryName(), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-5.0f, 0.0f),
                new InfusingBonus.Modifier(8.0f, 70.0f),
                new InfusingBonus.Modifier(4.0f, 60.0f)));
        infusingBonusMap.put(Items.GLOWSTONE_DUST.getRegistryName(), new InfusingBonus(
                COLOR_YELLOW,
                new InfusingBonus.Modifier(-2.0f, 0.0f),
                new InfusingBonus.Modifier(6.0f, 50.0f),
                new InfusingBonus.Modifier(3.0f, 50.0f)));
        infusingBonusMap.put(Items.BLAZE_POWDER.getRegistryName(), new InfusingBonus(
                COLOR_YELLOW,
                new InfusingBonus.Modifier(-6.0f, 0.0f),
                new InfusingBonus.Modifier(5.0f, 70.0f),
                new InfusingBonus.Modifier(5.0f, 70.0f)));
        infusingBonusMap.put(Items.QUARTZ.getRegistryName(), new InfusingBonus(
                COLOR_BLUE,
                new InfusingBonus.Modifier(-1.0f, 0.0f),
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(7.0f, 80.0f)));
        infusingBonusMap.put(Items.NETHER_STAR.getRegistryName(), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-60.0f, 0.0f),
                new InfusingBonus.Modifier(90.0f, 100.0f),
                new InfusingBonus.Modifier(90.0f, 100.0f)));
        infusingBonusMap.put(Items.GHAST_TEAR.getRegistryName(), new InfusingBonus(
                COLOR_YELLOW,
                new InfusingBonus.Modifier(-20.0f, 0.0f),
                new InfusingBonus.Modifier(25.0f, 100.0f),
                new InfusingBonus.Modifier(15.0f, 100.0f)));
        infusingBonusMap.put(Items.PRISMARINE_SHARD.getRegistryName(), new InfusingBonus(
                COLOR_YELLOW,
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(3.0f, 30.0f),
                new InfusingBonus.Modifier(3.0f, 30.0f)));
        infusingBonusMap.put(Items.PRISMARINE_CRYSTALS.getRegistryName(), new InfusingBonus(
                COLOR_YELLOW,
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(4.0f, 35.0f),
                new InfusingBonus.Modifier(4.0f, 35.0f)));
        infusingBonusMap.put(Items.SLIME_BALL.getRegistryName(), new InfusingBonus(
                COLOR_GREEN,
                InfusingBonus.Modifier.NONE,
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(-10.0f, 1.0f)));
        infusingBonusMap.put(Items.COAL.getRegistryName(), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-1.0f, 0.0f),
                new InfusingBonus.Modifier(-10.0f, 0.0f),
                InfusingBonus.Modifier.NONE));
        infusingBonusMap.put(Items.NETHER_WART.getRegistryName(), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(-3.0f, 0.0f),
                new InfusingBonus.Modifier(2.0f, 35.0f),
                new InfusingBonus.Modifier(-2.0f, 1.0f)));
        infusingBonusMap.put(Items.GOLD_INGOT.getRegistryName(), new InfusingBonus(
                COLOR_RED,
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(-1.0f, 0.0f),
                new InfusingBonus.Modifier(1.0f, 30.0f)));
        infusingBonusMap.put(Items.IRON_INGOT.getRegistryName(), new InfusingBonus(
                COLOR_RED,
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(-2.0f, 0.0f),
                new InfusingBonus.Modifier(1.0f, 20.0f)));
        infusingBonusMap.put(Items.SNOWBALL.getRegistryName(), new InfusingBonus(
                COLOR_RED,
                new InfusingBonus.Modifier(1.0f, 30.0f),
                InfusingBonus.Modifier.NONE,
                new InfusingBonus.Modifier(1.0f, 40.0f)));
        infusingBonusMap.put(new ResourceLocation("rftoolsbase", "dimensional_shard"), new InfusingBonus(
                COLOR_BLUE,
                new InfusingBonus.Modifier(1.0f, 100.0f),
                new InfusingBonus.Modifier(8.0f, 80.0f),
                new InfusingBonus.Modifier(8.0f, 80.0f)));
    }


    //    @Override
    protected void read(@Nonnull Map<ResourceLocation, JsonObject> objects, @Nonnull IResourceManager resourceManager, @Nonnull IProfiler profiler) {
        bonuses.clear();
        objects.forEach((rl, obj) -> bonuses.put(Ingredient.fromJson(obj.get("ingredient")), GSON.fromJson(obj.get("bonus"), InfusionBonus.class)));
    }

    public InfusionBonus getInfusionBonus(ItemStack stack) {
        return bonuses
                .entrySet()
                .stream()
                .filter(e -> e.getKey().test(stack))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(InfusionBonus.EMPTY);
    }

    public static String toString(InfusionBonus bonus) {
        return GSON.toJson(bonus);
    }

    public static InfusionBonus fromString(String s) {
        return GSON.fromJson(s, InfusionBonus.class);
    }

}
