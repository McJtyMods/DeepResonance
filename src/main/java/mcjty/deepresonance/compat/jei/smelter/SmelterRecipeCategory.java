package mcjty.deepresonance.compat.jei.smelter;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.jei.DeepResonanceJeiPlugin;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.lib.varia.ComponentFactory;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class SmelterRecipeCategory implements IRecipeCategory<SmelterRecipeWrapper> {

    private final IGuiHelper guiHelper;
    private final IDrawable slot;
    private final IDrawable arrow;
    private final IDrawable icon;
    private final IDrawable background;

    public static final ResourceLocation ID = new ResourceLocation(DeepResonance.MODID, "smelter");

    public SmelterRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        slot = guiHelper.getSlotDrawable();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachinesModule.SMELTER_BLOCK.get()));
        arrow = guiHelper.createDrawable(new ResourceLocation(DeepResonance.MODID, "textures/gui/guielements.png"),
                144, 0, 16, 16);
        background = guiHelper.createBlankDrawable(120, 60);
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return ComponentFactory.literal("Deep Resonance Smelter");
    }

    @Override
    @Nonnull
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public RecipeType<SmelterRecipeWrapper> getRecipeType() {
        return DeepResonanceJeiPlugin.SMELTER_RECIPE;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void draw(SmelterRecipeWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        slot.draw(stack, 20, 32);
        arrow.draw(stack, 46, 32);
        Font fontRenderer = Minecraft.getInstance().font;
        fontRenderer.draw(stack, "Tank below between", 10, 0, 0xffffffff);
        fontRenderer.draw(stack, "40% and 60% lava", 10, 10, 0xffffffff);
    }

    // @todo 1.19
//    @Override
//    public void setIngredients(SmelterRecipeWrapper recipe, IIngredients ingredients) {
//        ingredients.setInputLists(VanillaTypes.ITEM_STACK, Collections.singletonList(
//                List.of(new ItemStack(CoreModule.RESONATING_ORE_STONE_ITEM.get()),
//                        new ItemStack(CoreModule.RESONATING_ORE_END_ITEM.get()),
//                        new ItemStack(CoreModule.RESONATING_ORE_NETHER_ITEM.get()),
//                        new ItemStack(CoreModule.RESONATING_ORE_DEEPSLATE_ITEM.get()))));
//        ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(CoreModule.LIQUID_CRYSTAL.get(), SmelterConfig.RCL_PER_ORE.get()));
//    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, SmelterRecipeWrapper recipe, IFocusGroup focuses) {
        // @todo 1.19
//        IGuiItemStackGroup group = recipeLayout.getItemStacks();
//        group.init(0, true, 20, 32);
//        group.set(0, List.of(new ItemStack(CoreModule.RESONATING_ORE_DEEPSLATE_BLOCK.get()),
//                new ItemStack(CoreModule.RESONATING_ORE_END_BLOCK.get()),
//                new ItemStack(CoreModule.RESONATING_ORE_NETHER_BLOCK.get()),
//                new ItemStack(CoreModule.RESONATING_ORE_STONE_BLOCK.get())));
//        IGuiFluidStackGroup fluidGroup = recipeLayout.getFluidStacks();
//        fluidGroup.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
//            tooltip.add(ComponentFactory.literal("Purity: 10%").withStyle(ChatFormatting.GREEN));
//            tooltip.add(ComponentFactory.literal("Strength: 10%").withStyle(ChatFormatting.GREEN));
//            tooltip.add(ComponentFactory.literal("Efficiency: 10%").withStyle(ChatFormatting.GREEN));
//        });
//        fluidGroup.init(0, false, 70, 25, 30, 30, SmelterConfig.RCL_PER_ORE.get(), true, null);
//        fluidGroup.set(0, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
    }
}