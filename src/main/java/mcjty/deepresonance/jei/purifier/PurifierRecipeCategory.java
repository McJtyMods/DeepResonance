package mcjty.deepresonance.jei.purifier;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.config.ConfigMachines;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

public class PurifierRecipeCategory extends BlankRecipeCategory<PurifierRecipeWrapper> {

    private final IGuiHelper guiHelper;
    private final IDrawable slot;
    private final IDrawable arrow;

    public static final String ID = "DRPurifier";

    public PurifierRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        slot = guiHelper.getSlotDrawable();
        arrow = guiHelper.createDrawable(new ResourceLocation(DeepResonance.MODID, "textures/gui/guielements.png"),
                144, 0, 16, 16);
    }

    @Nonnull
    @Override
    public String getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Deep Resonance Purifier";
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return guiHelper.createBlankDrawable(120, 80);
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        super.drawExtras(minecraft);
        slot.draw(minecraft, 20, 10);
        arrow.draw(minecraft, 50, 10);
        slot.draw(minecraft, 80, 10);
    }

    // @todo @@@@@@@@@@@@@@
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PurifierRecipeWrapper recipeWrapper, IIngredients ingredients) {

    }
//    @Override
//    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull PurifierRecipeWrapper recipeWrapper) {
//        IGuiItemStackGroup group = recipeLayout.getItemStacks();
//        group.init(0, true, 20, 10);
//        group.set(0, recipeWrapper.getInputs());
//        group.init(1, false, 80, 10);
//        group.set(1, recipeWrapper.getOutputs());
//
//        IGuiFluidStackGroup fluidGroup = recipeLayout.getFluidStacks();
//        fluidGroup.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
//            if (slotIndex == 0) {
//                tooltip.add(TextFormatting.GREEN + "Purity: X");
//            } else {
//                tooltip.add(TextFormatting.GREEN + "Purity: X + 25%");
//            }
//        });
//        fluidGroup.init(0, true, 13, 35, 30, 30, ConfigMachines.Purifier.rclPerPurify, true, null);
//        fluidGroup.set(0, recipeWrapper.getFluidInputs());
//        fluidGroup.init(1, false, 73, 35, 30, 30, ConfigMachines.Purifier.rclPerPurify, true, null);
//        fluidGroup.set(1, recipeWrapper.getFluidOutputs());
//    }
}
