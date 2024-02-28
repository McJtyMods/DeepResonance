package mcjty.deepresonance.compat.jei.purifier;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.jei.DeepResonanceJeiPlugin;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.util.config.PurifierConfig;
import mcjty.deepresonance.modules.machines.util.config.SmelterConfig;
import mcjty.lib.varia.ComponentFactory;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;

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
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachinesModule.PURIFIER_BLOCK.get()));
        arrow = guiHelper.createDrawable(new ResourceLocation(DeepResonance.MODID, "textures/gui/guielements.png"),
                144, 0, 16, 16);
        background = guiHelper.createBlankDrawable(120, 80);
    }

    @Override
    @Nonnull
    public RecipeType<PurifierRecipeWrapper> getRecipeType() {
        return DeepResonanceJeiPlugin.PURIFIER_RECIPE;
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return ComponentFactory.literal("Deep Resonance Purifier");
    }

    @Override
    @Nonnull
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    @Nonnull
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void draw(PurifierRecipeWrapper recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        slot.draw(graphics, 20, 10);
        arrow.draw(graphics, 50, 10);
        slot.draw(graphics, 80, 10);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PurifierRecipeWrapper recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 10).addIngredients(VanillaTypes.ITEM_STACK,
                List.of(new ItemStack(CoreModule.FILTER_MATERIAL_ITEM.get())));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 10).addIngredients(VanillaTypes.ITEM_STACK,
                List.of(new ItemStack(CoreModule.SPENT_FILTER_ITEM.get())));
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 35)
                .setFluidRenderer(PurifierConfig.RCL_PER_PURIFY.get(), true, 30, 30)
                .addIngredients(ForgeTypes.FLUID_STACK,
                        List.of(new FluidStack(CoreModule.LIQUID_CRYSTAL.get(), SmelterConfig.RCL_PER_ORE.get())))
                .addTooltipCallback((view, tooltip) -> {
                    tooltip.add(ComponentFactory.literal("Purity: X").withStyle(ChatFormatting.GREEN));
                });
        builder.addSlot(RecipeIngredientRole.OUTPUT, 75, 35)
                .setFluidRenderer(PurifierConfig.RCL_PER_PURIFY.get(), true, 30, 30)
                .addIngredients(ForgeTypes.FLUID_STACK,
                        List.of(new FluidStack(CoreModule.LIQUID_CRYSTAL.get(), SmelterConfig.RCL_PER_ORE.get())))
                .addTooltipCallback((view, tooltip) -> {
                    tooltip.add(ComponentFactory.literal("Purity: X + 25%").withStyle(ChatFormatting.GREEN));
                });
    }
}
