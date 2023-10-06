package mcjty.deepresonance.compat.jei.laser;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.jei.DeepResonanceJeiPlugin;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.util.config.LaserConfig;
import mcjty.lib.varia.ComponentFactory;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class LaserRecipeCategory implements IRecipeCategory<LaserRecipeWrapper> {

    private final IGuiHelper guiHelper;
    private final IDrawable background;
    private final IDrawable slot;
    private final IDrawable icon;

    public static final ResourceLocation ID = new ResourceLocation(DeepResonance.MODID, "laser");

    public LaserRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        slot = guiHelper.getSlotDrawable();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachinesModule.LASER_BLOCK.get()));
        background = guiHelper.createBlankDrawable(150, 62);
    }

    @Override
    public RecipeType<LaserRecipeWrapper> getRecipeType() {
        return DeepResonanceJeiPlugin.LASER_RECIPE;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return ComponentFactory.literal("Deep Resonance Laser");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void draw(LaserRecipeWrapper recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        slot.draw(graphics);
        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, "Per " + LaserConfig.RCL_PER_CATALYST.get() + "mb RCL", 24, 0, 0xffffffff);
        graphics.drawString(font, "and " + LaserConfig.CRYSTAL_LIQUID_PER_CATALYST.get() + "mb crystal", 24, 10, 0xffffffff);
        recipe.drawInfo(graphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LaserRecipeWrapper recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).addIngredients(VanillaTypes.ITEM_STACK,
                List.of(recipe.getItem()));

        // @todo 1.19
//        IGuiItemStackGroup group = recipeLayout.getItemStacks();
//        group.init(0, true, 0, 0);
//        group.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
    }
}
