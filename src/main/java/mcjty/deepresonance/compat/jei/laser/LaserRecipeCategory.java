package mcjty.deepresonance.compat.jei.laser;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.util.config.LaserConfig;
import mcjty.lib.varia.ComponentFactory;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class LaserRecipeCategory implements IRecipeCategory<LaserRecipeWrapper> {

    private final IGuiHelper guiHelper;
    private final IDrawable background;
    private final IDrawable slot;
    private final IDrawable icon;

    public static final ResourceLocation ID = new ResourceLocation(DeepResonance.MODID, "laser");

    public LaserRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        slot = guiHelper.getSlotDrawable();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(MachinesModule.LASER_BLOCK.get()));
        background = guiHelper.createBlankDrawable(150, 62);
    }

    @Override
    public void setIngredients(LaserRecipeWrapper recipe, IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM_STACK, recipe.getItem());
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public Class<? extends LaserRecipeWrapper> getRecipeClass() {
        return LaserRecipeWrapper.class;
    }

    @Override
    public Component getTitle() {
        return ComponentFactory.literal("Deep Resonance Laser");
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void draw(LaserRecipeWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        slot.draw(stack);
        Font font = Minecraft.getInstance().font;
        font.draw(stack, "Per " + LaserConfig.RCL_PER_CATALYST.get() + "mb RCL", 24, 0, 0xffffffff);
        font.draw(stack, "and " + LaserConfig.CRYSTAL_LIQUID_PER_CATALYST.get() + "mb crystal", 24, 10, 0xffffffff);
        recipe.drawInfo(stack);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, LaserRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, true, 0, 0);
        group.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
    }
}
