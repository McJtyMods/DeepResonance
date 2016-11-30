package mcjty.deepresonance.jei;

import mezz.jei.api.*;

import javax.annotation.Nonnull;

@JEIPlugin
public class DeepResonanceJeiPlugin extends BlankModPlugin {

    @Override
    public void register(@Nonnull IModRegistry registry) {
        // @todo @@@@@@@@@@@@@@@@@
//        IJeiHelpers helpers = registry.getJeiHelpers();
//        IGuiHelper guiHelper = helpers.getGuiHelper();
//
//        registry.addRecipeCategories(
//                new LaserRecipeCategory(guiHelper),
//                new SmelterRecipeCategory(guiHelper),
//                new PurifierRecipeCategory(guiHelper));
//        registry.addRecipeHandlers(
//                new LaserRecipeHandler(),
//                new SmelterRecipeHandler(),
//                new PurifierRecipeHandler());
//
//        List<IRecipeWrapper> recipes = new ArrayList<>();
//        for (String registryName : LaserTileEntity.infusingBonusMap.keySet()) {
//            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
//            recipes.add(new LaserRecipeWrapper(item));
//        }
//        recipes.add(new SmelterRecipeWrapper());
//        recipes.add(new PurifierRecipeWrapper());
//        registry.addRecipes(recipes);
//
//        registry.addRecipeCategoryCraftingItem(new ItemStack(LaserSetup.laserBlock), LaserRecipeCategory.ID);
//        registry.addRecipeCategoryCraftingItem(new ItemStack(SmelterSetup.smelter), SmelterRecipeCategory.ID);
//        registry.addRecipeCategoryCraftingItem(new ItemStack(PurifierSetup.purifierBlock), PurifierRecipeCategory.ID);
    }
}
