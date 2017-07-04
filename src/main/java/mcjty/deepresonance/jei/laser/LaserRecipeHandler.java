package mcjty.deepresonance.jei.laser;

import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class LaserRecipeHandler implements mezz.jei.api.recipe.IRecipeHandler<LaserRecipeWrapper> {

    private final String id;

    public LaserRecipeHandler() {
        this.id = LaserRecipeCategory.ID;
    }

    @Nonnull
    @Override
    public Class<LaserRecipeWrapper> getRecipeClass() {
        return LaserRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull LaserRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public String getRecipeCategoryUid(LaserRecipeWrapper recipe) {
        return id;
    }

    @Override
    public boolean isRecipeValid(LaserRecipeWrapper recipe) {
        return true;
    }
}
