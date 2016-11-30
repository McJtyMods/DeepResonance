package mcjty.deepresonance.jei.smelter;

import mcjty.lib.jei.CompatRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class SmelterRecipeHandler extends CompatRecipeHandler<SmelterRecipeWrapper> {

    public SmelterRecipeHandler() {
        super(SmelterRecipeCategory.ID);
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
}
