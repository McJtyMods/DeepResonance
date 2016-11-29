package mcjty.deepresonance.jei.smelter;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class SmelterRecipeHandler implements IRecipeHandler<SmelterRecipeWrapper> {

    @Nonnull
    @Override
    public Class<SmelterRecipeWrapper> getRecipeClass() {
        return SmelterRecipeWrapper.class;
    }

    // @todo @@@@@@@@@@@@@@@@@
    @Nonnull
//    @Override
    public String getRecipeCategoryUid() {
        return SmelterRecipeCategory.ID;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid(@Nonnull SmelterRecipeWrapper recipe) {
        return SmelterRecipeCategory.ID;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull SmelterRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull SmelterRecipeWrapper recipe) {
        return true;
    }
}
