package mcjty.deepresonance.compat.jei.smelter;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.jei.DeepResonanceJeiPlugin;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;

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

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SmelterRecipeWrapper recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 32)
                .addIngredients(VanillaTypes.ITEM_STACK,
                        List.of(new ItemStack(CoreModule.RESONATING_ORE_DEEPSLATE_BLOCK.get()),
                                new ItemStack(CoreModule.RESONATING_ORE_END_BLOCK.get()),
                                new ItemStack(CoreModule.RESONATING_ORE_NETHER_BLOCK.get()),
                                new ItemStack(CoreModule.RESONATING_ORE_STONE_BLOCK.get())));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 25)
                .setFluidRenderer(SmelterConfig.RCL_PER_ORE.get(), true, 30, 30)
                .addIngredients(ForgeTypes.FLUID_STACK,
                        List.of(new FluidStack(CoreModule.LIQUID_CRYSTAL.get(), SmelterConfig.RCL_PER_ORE.get())))
                .addTooltipCallback((view, tooltip) -> {
                    tooltip.add(ComponentFactory.literal("Purity: 10%").withStyle(ChatFormatting.GREEN));
                    tooltip.add(ComponentFactory.literal("Strength: 10%").withStyle(ChatFormatting.GREEN));
                    tooltip.add(ComponentFactory.literal("Efficiency: 10%").withStyle(ChatFormatting.GREEN));
                });
    }
}