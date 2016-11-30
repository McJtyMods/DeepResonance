package mcjty.deepresonance.jei.smelter;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;

public class SmelterRecipeWrapper extends BlankRecipeWrapper {

    public SmelterRecipeWrapper() {
    }

    // @todo @@@@@@@@@@@@
    @Override
    public void getIngredients(IIngredients ingredients) {
    }

//    @Nonnull
//    @Override
//    public List getInputs() {
//        List<ItemStack> input = new ArrayList<>();
//        input.add(new ItemStack(ModBlocks.resonatingOreBlock));
//        return input;
//    }
//
//    @Nonnull
//    @Override
//    public List<FluidStack> getFluidOutputs() {
//        List<FluidStack> input = new ArrayList<>();
//        FluidStack fluidStack = LiquidCrystalFluidTagData.makeLiquidCrystalStack(ConfigMachines.Smelter.rclPerOre, 1.0f, 0.1f, 0.1f, 0.1f);
//        input.add(fluidStack);
//        return input;
//    }
}
