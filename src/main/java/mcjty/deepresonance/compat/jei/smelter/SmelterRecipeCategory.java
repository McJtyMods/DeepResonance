package mcjty.deepresonance.compat.jei.smelter;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.util.config.SmelterConfig;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Collections;

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
        icon = guiHelper.createDrawableIngredient(new ItemStack(MachinesModule.SMELTER_BLOCK.get()));
        arrow = guiHelper.createDrawable(new ResourceLocation(DeepResonance.MODID, "textures/gui/guielements.png"),
                144, 0, 16, 16);
        background = guiHelper.createBlankDrawable(120, 60);
    }

    @Override
    public String getTitle() {
        return "Deep Resonance Smelter";
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
    public Class<? extends SmelterRecipeWrapper> getRecipeClass() {
        return SmelterRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void draw(SmelterRecipeWrapper recipe, MatrixStack stack, double mouseX, double mouseY) {
        slot.draw(stack, 20, 32);
        arrow.draw(stack, 46, 32);
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        fontRenderer.draw(stack, "Tank below between", 10, 0, 0xffffffff);
        fontRenderer.draw(stack, "40% and 60% lava", 10, 10, 0xffffffff);
    }

    @Override
    public void setIngredients(SmelterRecipeWrapper recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(
                Lists.newArrayList(new ItemStack(CoreModule.RESONATING_ORE_STONE_ITEM.get()),
                        new ItemStack(CoreModule.RESONATING_ORE_END_ITEM.get()),
                        new ItemStack(CoreModule.RESONATING_ORE_NETHER_ITEM.get()))));
        ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(CoreModule.LIQUID_CRYSTAL.get(), SmelterConfig.RCL_PER_ORE.get()));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SmelterRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, true, 20, 32);
        group.set(0, Lists.newArrayList(
                new ItemStack(CoreModule.RESONATING_ORE_END_BLOCK.get()),
                new ItemStack(CoreModule.RESONATING_ORE_NETHER_BLOCK.get()),
                new ItemStack(CoreModule.RESONATING_ORE_STONE_BLOCK.get())));
        IGuiFluidStackGroup fluidGroup = recipeLayout.getFluidStacks();
        fluidGroup.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            tooltip.add(new StringTextComponent("Purity: 10%").withStyle(TextFormatting.GREEN));
            tooltip.add(new StringTextComponent("Strength: 10%").withStyle(TextFormatting.GREEN));
            tooltip.add(new StringTextComponent("Efficiency: 10%").withStyle(TextFormatting.GREEN));
        });
        fluidGroup.init(0, false, 70, 25, 30, 30, SmelterConfig.RCL_PER_ORE.get(), true, null);
        fluidGroup.set(0, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
    }
}