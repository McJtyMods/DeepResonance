package mcjty.deepresonance.api.infusion;

import net.minecraft.data.DataGenerator;

public abstract class AbstractInfusionBonusProvider {

    public AbstractInfusionBonusProvider(DataGenerator generatorIn) {
//        super(generatorIn, "infusion_bonus");
    }

    // @todo 1.16
//    @Override
//    public final void registerObjects(BiConsumer<ResourceLocation, JsonObject> registry) {
//        registerObjects((rl, ingredient, bonus) -> {
//            JsonObject object = new JsonObject();
//            object.add("ingredient", Preconditions.checkNotNull(ingredient).serialize());
//            object.add("bonus", GSON.toJsonTree(Preconditions.checkNotNull(bonus)));
//            registry.accept(rl, object);
//        });
//    }
//
//    protected abstract void registerObjects(TriConsumer<ResourceLocation, Ingredient, InfusionBonus> registry);
//
//    @Nonnull
//    @Override
//    public String providerName() {
//        return "Infusion bonuses";
//    }

}
