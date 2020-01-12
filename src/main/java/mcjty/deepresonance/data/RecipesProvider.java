package mcjty.deepresonance.data;

import mcjty.lib.datagen.BaseRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 10-1-2020
 */
public class RecipesProvider extends BaseRecipeProvider {

    RecipesProvider(DataGenerator datagen) {
        super(datagen);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {

    }

}
