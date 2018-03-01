package mcjty.deepresonance.jei.laser;

import elec332.core.client.RenderHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.config.ConfigMachines;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class LaserRecipeCategory extends BlankRecipeCategory<LaserRecipeWrapper> {

    private final IGuiHelper guiHelper;
    private final IDrawable slot;

    public static final String ID = "DRLaser";

    public LaserRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        slot = guiHelper.getSlotDrawable();
    }

    @Nonnull
    @Override
    public String getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Deep Resonance Laser";
    }

    @Override
    public String getModName() {
        return DeepResonance.MODID;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        IDrawableStatic drawable = guiHelper.createBlankDrawable(120, 70);
        return drawable;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        super.drawExtras(minecraft);
        slot.draw(minecraft);
        RenderHelper.getMCFontrenderer().drawString("Per " + ConfigMachines.laser.rclPerCatalyst + "mb RCL", 24, 0, 0xffffffff, true);
        RenderHelper.getMCFontrenderer().drawString("and " + ConfigMachines.laser.crystalLiquidPerCatalyst + "mb crystal", 24, 10, 0xffffffff, true);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, LaserRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, true, 0, 0);
        group.set(0, ingredients.getInputs(ItemStack.class).get(0));
    }
}
