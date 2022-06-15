package mcjty.deepresonance.compat.jei;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.jei.laser.LaserRecipeCategory;
import mcjty.deepresonance.compat.jei.laser.LaserRecipeWrapper;
import mcjty.deepresonance.compat.jei.purifier.PurifierRecipeCategory;
import mcjty.deepresonance.compat.jei.purifier.PurifierRecipeWrapper;
import mcjty.deepresonance.compat.jei.smelter.SmelterRecipeCategory;
import mcjty.deepresonance.compat.jei.smelter.SmelterRecipeWrapper;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class DeepResonanceJeiPlugin implements IModPlugin {

    public static final ResourceLocation LASER_RECIPE = new ResourceLocation(DeepResonance.MODID, "laser");
    public static final ResourceLocation PURIFIER_RECIPE = new ResourceLocation(DeepResonance.MODID, "purifier");
    public static final ResourceLocation SMELTER_RECIPE = new ResourceLocation(DeepResonance.MODID, "smelter");

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(DeepResonance.MODID, "jeiplugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MachinesModule.LASER_BLOCK.get()), LASER_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(MachinesModule.PURIFIER_BLOCK.get()), PURIFIER_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(MachinesModule.SMELTER_BLOCK.get()), SMELTER_RECIPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(new LaserRecipeCategory(guiHelper));
        registration.addRecipeCategories(new PurifierRecipeCategory(guiHelper));
        registration.addRecipeCategories(new SmelterRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<LaserRecipeWrapper> recipes = new ArrayList<>();
        InfusionBonusRegistry.getInfusingBonusMap().forEach((id, bonus) -> {
            recipes.add(new LaserRecipeWrapper(new ItemStack(ForgeRegistries.ITEMS.getValue(id))));
        });
        registration.addRecipes(recipes, LASER_RECIPE);
        registration.addRecipes(Collections.singletonList(new PurifierRecipeWrapper()), PURIFIER_RECIPE);
        registration.addRecipes(Collections.singletonList(new SmelterRecipeWrapper()), SMELTER_RECIPE);
    }
}
