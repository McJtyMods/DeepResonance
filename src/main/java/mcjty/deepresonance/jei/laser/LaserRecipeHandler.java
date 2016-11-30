package mcjty.deepresonance.jei.laser;

import mcjty.lib.jei.CompatRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class LaserRecipeHandler extends CompatRecipeHandler<LaserRecipeWrapper> {

    public LaserRecipeHandler() {
        super(LaserRecipeCategory.ID);
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
}
