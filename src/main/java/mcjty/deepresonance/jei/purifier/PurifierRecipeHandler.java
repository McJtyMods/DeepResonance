package mcjty.deepresonance.jei.purifier;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class PurifierRecipeHandler implements IRecipeHandler<PurifierRecipeWrapper> {

    @Nonnull
    @Override
    public Class<PurifierRecipeWrapper> getRecipeClass() {
        return PurifierRecipeWrapper.class;
    }

    // @todo @@@@@@@@@@@@@@@@@@@@@
    @Nonnull
//    @Override
    public String getRecipeCategoryUid() {
        return PurifierRecipeCategory.ID;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid(@Nonnull PurifierRecipeWrapper recipe) {
        return PurifierRecipeCategory.ID;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull PurifierRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull PurifierRecipeWrapper recipe) {
        return true;
    }
}
