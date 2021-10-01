package mcjty.deepresonance.modules.machines.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import mcjty.deepresonance.api.infusion.InfusionBonus;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;

public class InfusionBonusRegistry {

    private final Map<Ingredient, InfusionBonus> bonuses;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public InfusionBonusRegistry() {
        this.bonuses = Maps.newHashMap();
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
