package mcjty.deepresonance.compat.jei;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.jei.laser.LaserRecipeCategory;
import mcjty.deepresonance.compat.jei.laser.LaserRecipeHandler;
import mcjty.deepresonance.compat.jei.laser.LaserRecipeWrapper;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class DeepResonanceJeiPlugin implements IModPlugin {

    public static final RecipeType<LaserRecipeWrapper> LASER_RECIPE = RecipeType.create(DeepResonance.MODID, "laser", LaserRecipeWrapper.class);

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(DeepResonance.MODID, "jeiplugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MachinesModule.LASER_BLOCK.get()), LASER_RECIPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(new LaserRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new LaserRecipeHandler(), LASER_RECIPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<LaserRecipeWrapper> recipes = new ArrayList<>();
        InfusionBonusRegistry.getInfusingBonusMap().forEach((id, bonus) -> {
            recipes.add(new LaserRecipeWrapper(new ItemStack(ForgeRegistries.ITEMS.getValue(id))));
        });
        registration.addRecipes(LASER_RECIPE, recipes);
    }
}
