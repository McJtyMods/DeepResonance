package mcjty.deepresonance.compat.jei.laser;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.machines.data.InfusingBonus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

public class LaserRecipeWrapper {

    private final ItemStack item;

    public LaserRecipeWrapper(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void drawInfo(MatrixStack stack) {
        InfusingBonus bonus = LaserTileEntity.getInfusingBonus(item);

        renderStat(stack, "Purity:", bonus.getPurityModifier(), 30);
        renderStat(stack, "Strength:", bonus.getStrengthModifier(), 40);
        renderStat(stack, "Efficiency:", bonus.getEfficiencyModifier(), 50);
    }

    private void renderStat(MatrixStack stack, String label, InfusingBonus.Modifier modifier, int y) {
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        fontRenderer.draw(stack, label, 0, y, 0xffffffff);
        float purityBonus = modifier.getBonus();
        fontRenderer.draw(stack, String.valueOf(purityBonus)+"%", 60, y,
                purityBonus > 0 ? 0xff006600 : 0xffff0000);
        fontRenderer.draw(stack, "(" + String.valueOf(modifier.getMaxOrMin()) + ")", 100, y,
                0xff000000);
    }

}
