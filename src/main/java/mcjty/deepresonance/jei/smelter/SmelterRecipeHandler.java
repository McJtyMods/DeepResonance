package mcjty.deepresonance.jei.smelter;

import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class SmelterRecipeHandler implements mezz.jei.api.recipe.IRecipeHandler<SmelterRecipeWrapper> {

    private final String id;

    public SmelterRecipeHandler() {
        this.id = SmelterRecipeCategory.ID;
    }

    @Nonnull
    @Override
    public Class<SmelterRecipeWrapper> getRecipeClass() {
        return SmelterRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull SmelterRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public String getRecipeCategoryUid(SmelterRecipeWrapper recipe) {
        return id;
    }

    @Override
    public boolean isRecipeValid(SmelterRecipeWrapper recipe) {
        return true;
    }
}
