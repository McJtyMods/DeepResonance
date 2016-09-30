package mcjty.deepresonance.jei.laser;

import elec332.core.client.RenderHelper;
import mcjty.deepresonance.blocks.laser.InfusingBonus;
import mcjty.deepresonance.blocks.laser.LaserTileEntity;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LaserRecipeWrapper extends BlankRecipeWrapper {

    private final Item item;

    public LaserRecipeWrapper(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Nonnull
    @Override
    public List getInputs() {
        List<ItemStack> input = new ArrayList<>();
        input.add(new ItemStack(item));
        return input;
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
        RenderHelper.getMCFontrenderer().drawString(label, 0, y, 0xffffffff, true);
        float purityBonus = modifier.getBonus();
        RenderHelper.getMCFontrenderer().drawString(String.valueOf(purityBonus)+"%", 60, y,
                purityBonus > 0 ? 0xff006600 : 0xffff0000, false);
        RenderHelper.getMCFontrenderer().drawString("(" + String.valueOf(modifier.getMaxOrMin()) + ")", 100, y,
                0xff000000, false);
    }

}
