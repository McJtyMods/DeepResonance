package mcjty.deepresonance.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class LaserRecipeHandler implements IRecipeHandler<LaserRecipeWrapper> {

    @Nonnull
    @Override
    public Class<LaserRecipeWrapper> getRecipeClass() {
        return LaserRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid() {
        return LaserRecipeCategory.ID;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid(@Nonnull LaserRecipeWrapper recipe) {
        return LaserRecipeCategory.ID;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull LaserRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull LaserRecipeWrapper recipe) {
        return true;
    }
}
