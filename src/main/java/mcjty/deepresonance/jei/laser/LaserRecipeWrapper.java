package mcjty.deepresonance.jei.laser;

import mcjty.deepresonance.blocks.laser.InfusingBonus;
import mcjty.deepresonance.blocks.laser.LaserTileEntity;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class LaserRecipeWrapper extends BlankRecipeWrapper {

    private final Item item;

    public LaserRecipeWrapper(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, new ItemStack(item));
    }


    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
        InfusingBonus bonus = LaserTileEntity.infusingBonusMap.get(item.getRegistryName().toString());

        renderStat("Purity:", bonus.getPurityModifier(), 30);
        renderStat("Strength:", bonus.getStrengthModifier(), 40);
        renderStat("Efficiency:", bonus.getEfficiencyModifier(), 50);
    }

    private void renderStat(String label, InfusingBonus.Modifier modifier, int y) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString(label, 0, y, 0xffffffff, true);
        float purityBonus = modifier.getBonus();
        fontRenderer.drawString(String.valueOf(purityBonus)+"%", 60, y,
                purityBonus > 0 ? 0xff006600 : 0xffff0000, false);
        fontRenderer.drawString("(" + String.valueOf(modifier.getMaxOrMin()) + ")", 100, y,
                0xff000000, false);
    }

}
