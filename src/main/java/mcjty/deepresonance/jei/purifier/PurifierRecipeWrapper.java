package mcjty.deepresonance.jei.purifier;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;

public class PurifierRecipeWrapper extends BlankRecipeWrapper {

    public PurifierRecipeWrapper() {
    }

    // @todo @@@@@@@@@@@@
    @Override
    public void getIngredients(IIngredients ingredients) {
    }

//    @Nonnull
//    @Override
//    public List getInputs() {
//        List<ItemStack> input = new ArrayList<>();
//        input.add(new ItemStack(ModItems.filterMaterialItem));
//        return input;
//    }
//
//    @Nonnull
//    @Override
//    public List getOutputs() {
//        List<ItemStack> input = new ArrayList<>();
//        input.add(new ItemStack(ModItems.spentFilterMaterialItem));
//        return input;
//    }
//
//    @Nonnull
//    @Override
//    public List<FluidStack> getFluidOutputs() {
//        List<FluidStack> input = new ArrayList<>();
//        FluidStack fluidStack = LiquidCrystalFluidTagData.makeLiquidCrystalStack(ConfigMachines.Purifier.rclPerPurify, 1.0f, 0.35f, 0.1f, 0.1f);
//        input.add(fluidStack);
//        return input;
//    }
//
//    @Nonnull
//    @Override
//    public List<FluidStack> getFluidInputs() {
//        List<FluidStack> input = new ArrayList<>();
//        FluidStack fluidStack = LiquidCrystalFluidTagData.makeLiquidCrystalStack(ConfigMachines.Purifier.rclPerPurify, 1.0f, 0.1f, 0.1f, 0.1f);
//        input.add(fluidStack);
//        return input;
//    }
}
