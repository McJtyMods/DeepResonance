package mcjty.deepresonance.jei;

import mcjty.deepresonance.blocks.laser.LaserSetup;
import mcjty.deepresonance.blocks.laser.LaserTileEntity;
import mcjty.deepresonance.blocks.purifier.PurifierSetup;
import mcjty.deepresonance.blocks.smelter.SmelterSetup;
import mcjty.deepresonance.jei.laser.LaserRecipeCategory;
import mcjty.deepresonance.jei.laser.LaserRecipeHandler;
import mcjty.deepresonance.jei.laser.LaserRecipeWrapper;
import mcjty.deepresonance.jei.purifier.PurifierRecipeCategory;
import mcjty.deepresonance.jei.purifier.PurifierRecipeHandler;
import mcjty.deepresonance.jei.purifier.PurifierRecipeWrapper;
import mcjty.deepresonance.jei.smelter.SmelterRecipeCategory;
import mcjty.deepresonance.jei.smelter.SmelterRecipeHandler;
import mcjty.deepresonance.jei.smelter.SmelterRecipeWrapper;
import mcjty.lib.jei.JeiCompatTools;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class DeepResonanceJeiPlugin extends BlankModPlugin {

    @Override
    public void register(@Nonnull IModRegistry registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = helpers.getGuiHelper();

        registry.addRecipeCategories(
                new LaserRecipeCategory(guiHelper),
                new SmelterRecipeCategory(guiHelper),
                new PurifierRecipeCategory(guiHelper));
        registry.addRecipeHandlers(
                new LaserRecipeHandler(),
                new SmelterRecipeHandler(),
                new PurifierRecipeHandler());

        List<IRecipeWrapper> recipes = new ArrayList<>();
        for (String registryName : LaserTileEntity.infusingBonusMap.keySet()) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
            recipes.add(new LaserRecipeWrapper(item));
        }
        recipes.add(new SmelterRecipeWrapper());
        recipes.add(new PurifierRecipeWrapper());
        JeiCompatTools.addRecipes(registry, recipes);

        registry.addRecipeCategoryCraftingItem(new ItemStack(LaserSetup.laserBlock), LaserRecipeCategory.ID);
        registry.addRecipeCategoryCraftingItem(new ItemStack(SmelterSetup.smelter), SmelterRecipeCategory.ID);
        registry.addRecipeCategoryCraftingItem(new ItemStack(PurifierSetup.purifierBlock), PurifierRecipeCategory.ID);
    }
}
