package mcjty.deepresonance.compat.jei.purifier;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.jei.laser.LaserRecipeWrapper;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.util.config.PurifierConfig;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.varia.ComponentFactory;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class PurifierRecipeCategory implements IRecipeCategory<PurifierRecipeWrapper> {

    private final IGuiHelper guiHelper;
    private final IDrawable background;
    private final IDrawable slot;
    private final IDrawable arrow;
    private final IDrawable icon;

    public static final ResourceLocation ID = new ResourceLocation(DeepResonance.MODID, "purifier");

    public PurifierRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        slot = guiHelper.getSlotDrawable();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(MachinesModule.PURIFIER_BLOCK.get()));
        arrow = guiHelper.createDrawable(new ResourceLocation(DeepResonance.MODID, "textures/gui/guielements.png"),
                144, 0, 16, 16);
        background = guiHelper.createBlankDrawable(120, 80);
    }

    @Override
    public Component getTitle() {
        return ComponentFactory.literal("Deep Resonance Purifier");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends PurifierRecipeWrapper> getRecipeClass() {
        return PurifierRecipeWrapper.class;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void draw(PurifierRecipeWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        slot.draw(stack, 20, 10);
        arrow.draw(stack, 50, 10);
        slot.draw(stack, 80, 10);
    }

    @Override
    public void setIngredients(PurifierRecipeWrapper recipe, IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM_STACK, new ItemStack(CoreModule.FILTER_MATERIAL_ITEM.get()));
        ingredients.setOutput(VanillaTypes.ITEM_STACK, new ItemStack(CoreModule.SPENT_FILTER_ITEM.get()));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PurifierRecipeWrapper recipe, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, true, 20, 10);
        group.set(0, new ItemStack(CoreModule.FILTER_MATERIAL_ITEM.get()));
        group.init(1, false, 80, 10);
        group.set(1, new ItemStack(CoreModule.SPENT_FILTER_ITEM.get()));

        IGuiFluidStackGroup fluidGroup = recipeLayout.getFluidStacks();
        fluidGroup.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == 0) {
                tooltip.add(ComponentFactory.literal("Purity: X").withStyle(ChatFormatting.GREEN));
            } else {
                tooltip.add(ComponentFactory.literal("Purity: X + 25%").withStyle(ChatFormatting.GREEN));
            }
        });
        FluidStack input = LiquidCrystalData.makeLiquidCrystalStack(PurifierConfig.RCL_PER_PURIFY.get(), 1.0f, 0.1f, 0.1f, 0.1f);
        FluidStack output = LiquidCrystalData.makeLiquidCrystalStack(PurifierConfig.RCL_PER_PURIFY.get(), 1.0f, 0.35f, 0.1f, 0.1f);
        fluidGroup.init(0, true, 13, 35, 30, 30, PurifierConfig.RCL_PER_PURIFY.get(), true, null);
        fluidGroup.set(0, input);
        fluidGroup.init(1, false, 73, 35, 30, 30, PurifierConfig.RCL_PER_PURIFY.get(), true, null);
        fluidGroup.set(1, output);
    }
}
