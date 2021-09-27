package mcjty.deepresonance.api.infusion;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

/**
 * Created by Elec332 on 28-7-2020
 */
public abstract class AbstractInfusionBonusProvider extends AbstractDataProvider {

    public AbstractInfusionBonusProvider(DataGenerator generatorIn) {
        super(generatorIn, "infusion_bonus");
    }

    @Override
    public final void registerObjects(BiConsumer<ResourceLocation, JsonObject> registry) {
        registerObjects((rl, ingredient, bonus) -> {
            JsonObject object = new JsonObject();
            object.add("ingredient", Preconditions.checkNotNull(ingredient).serialize());
            object.add("bonus", GSON.toJsonTree(Preconditions.checkNotNull(bonus)));
            registry.accept(rl, object);
        });
    }

    protected abstract void registerObjects(TriConsumer<ResourceLocation, Ingredient, InfusionBonus> registry);

    @Nonnull
    @Override
    public String providerName() {
        return "Infusion bonuses";
    }

}
