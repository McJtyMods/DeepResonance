package mcjty.deepresonance.jei.purifier;

import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.deepresonance.items.ModItems;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class PurifierRecipeWrapper extends BlankRecipeWrapper {

    public PurifierRecipeWrapper() {
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, new ItemStack(ModItems.filterMaterialItem));
        ingredients.setOutput(ItemStack.class, new ItemStack(ModItems.spentFilterMaterialItem));
        FluidStack fluidStackIn = LiquidCrystalFluidTagData.makeLiquidCrystalStack(ConfigMachines.purifier.rclPerPurify, 1.0f, 0.1f, 0.1f, 0.1f);
        ingredients.setInput(FluidStack.class, fluidStackIn);
        FluidStack fluidStackOut = LiquidCrystalFluidTagData.makeLiquidCrystalStack(ConfigMachines.purifier.rclPerPurify, 1.0f, 0.35f, 0.1f, 0.1f);
        ingredients.setOutput(FluidStack.class, fluidStackOut);
    }
}
