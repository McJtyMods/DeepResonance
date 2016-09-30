package mcjty.deepresonance.jei;

import elec332.core.client.RenderHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.config.ConfigMachines;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

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
        RenderHelper.getMCFontrenderer().drawString("Tank below between", 10, 0, 0xffffffff, true);
        RenderHelper.getMCFontrenderer().drawString("40% and 60% lava", 10, 10, 0xffffffff, true);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull SmelterRecipeWrapper recipeWrapper) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, true, 20, 32);
        group.set(0, recipeWrapper.getInputs());
        IGuiFluidStackGroup fluidGroup = recipeLayout.getFluidStacks();
        fluidGroup.init(0, false, 70, 25, 30, 30, ConfigMachines.Smelter.rclPerOre, true, null);
        fluidGroup.set(0, recipeWrapper.getFluidOutputs());
    }
}
