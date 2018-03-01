package mcjty.deepresonance.jei.smelter;

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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class SmelterRecipeCategory extends BlankRecipeCategory<SmelterRecipeWrapper> {

    private final IGuiHelper guiHelper;
    private final IDrawable slot;
    private final IDrawable arrow;

    public static final String ID = "DRSmelter";

    public SmelterRecipeCategory(IGuiHelper guiHelper) {
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
        return "Deep Resonance Smelter";
    }

    @Override
    public String getModName() {
        return DeepResonance.MODID;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return guiHelper.createBlankDrawable(120, 60);
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        super.drawExtras(minecraft);
        slot.draw(minecraft, 20, 32);
        arrow.draw(minecraft, 46, 32);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString("Tank below between", 10, 0, 0xffffffff, true);
        fontRenderer.drawString("40% and 60% lava", 10, 10, 0xffffffff, true);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SmelterRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, true, 20, 32);
        group.set(0, ingredients.getInputs(ItemStack.class).get(0));
        IGuiFluidStackGroup fluidGroup = recipeLayout.getFluidStacks();
        fluidGroup.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            tooltip.add(TextFormatting.GREEN + "Purity: 10%");
            tooltip.add(TextFormatting.GREEN + "Strength: 10%");
            tooltip.add(TextFormatting.GREEN + "Efficiency: 10%");
        });
        fluidGroup.init(0, false, 70, 25, 30, 30, ConfigMachines.smelter.rclPerOre, true, null);
        fluidGroup.set(0, ingredients.getOutputs(FluidStack.class).get(0));
    }
}
