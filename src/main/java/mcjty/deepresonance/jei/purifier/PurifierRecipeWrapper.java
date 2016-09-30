package mcjty.deepresonance.jei.purifier;

import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.deepresonance.items.ModItems;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PurifierRecipeWrapper extends BlankRecipeWrapper {

    public PurifierRecipeWrapper() {
    }

    @Nonnull
    @Override
    public List getInputs() {
        List<ItemStack> input = new ArrayList<>();
        input.add(new ItemStack(ModItems.filterMaterialItem));
        return input;
    }

    @Nonnull
    @Override
    public List getOutputs() {
        List<ItemStack> input = new ArrayList<>();
        input.add(new ItemStack(ModItems.spentFilterMaterialItem));
        return input;
    }

    @Nonnull
    @Override
    public List<FluidStack> getFluidOutputs() {
        List<FluidStack> input = new ArrayList<>();
        FluidStack fluidStack = LiquidCrystalFluidTagData.makeLiquidCrystalStack(ConfigMachines.Purifier.rclPerPurify, 1.0f, 0.35f, 0.1f, 0.1f);
        input.add(fluidStack);
        return input;
    }

    @Nonnull
    @Override
    public List<FluidStack> getFluidInputs() {
        List<FluidStack> input = new ArrayList<>();
        FluidStack fluidStack = LiquidCrystalFluidTagData.makeLiquidCrystalStack(ConfigMachines.Purifier.rclPerPurify, 1.0f, 0.1f, 0.1f, 0.1f);
        input.add(fluidStack);
        return input;
    }
}
