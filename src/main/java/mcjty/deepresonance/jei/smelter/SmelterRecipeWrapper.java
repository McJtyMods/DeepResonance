package mcjty.deepresonance.jei.smelter;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
