package mcjty.deepresonance.jei.purifier;

import mcjty.lib.jei.CompatRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class PurifierRecipeHandler extends CompatRecipeHandler<PurifierRecipeWrapper> {

    public PurifierRecipeHandler() {
        super(PurifierRecipeCategory.ID);
    }

    @Nonnull
    @Override
    public Class<PurifierRecipeWrapper> getRecipeClass() {
        return PurifierRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull PurifierRecipeWrapper recipe) {
        return recipe;
    }
}
