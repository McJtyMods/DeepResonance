package mcjty.deepresonance.compat.jei.smelter;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class SmelterRecipeWrapper extends BlankRecipeWrapper {

    public SmelterRecipeWrapper() {
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, new ItemStack(ModBlocks.resonatingOreBlock));
        FluidStack fluidStack = LiquidCrystalFluidTagData.makeLiquidCrystalStack(ConfigMachines.smelter.rclPerOre, 1.0f, 0.1f, 0.1f, 0.1f);
        ingredients.setOutput(FluidStack.class, fluidStack);
    }
}
