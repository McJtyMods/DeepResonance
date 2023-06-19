package mcjty.deepresonance.compat.jei.laser;

import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.machines.data.InfusingBonus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class LaserRecipeWrapper {

    private final ItemStack item;

    public LaserRecipeWrapper(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void drawInfo(GuiGraphics graphics) {
        InfusingBonus bonus = LaserTileEntity.getInfusingBonus(item);

        renderStat(graphics, "Purity:", bonus.purityModifier(), 30);
        renderStat(graphics, "Strength:", bonus.strengthModifier(), 40);
        renderStat(graphics, "Efficiency:", bonus.efficiencyModifier(), 50);
    }

    private void renderStat(GuiGraphics graphics, String label, InfusingBonus.Modifier modifier, int y) {
        Font fontRenderer = Minecraft.getInstance().font;
        graphics.drawString(fontRenderer, label, 0, y, 0xffffffff);
        float purityBonus = modifier.bonus();
        graphics.drawString(fontRenderer, String.valueOf(purityBonus)+"%", 60, y,
                purityBonus > 0 ? 0xff006600 : 0xffff0000);
        graphics.drawString(fontRenderer, "(" + String.valueOf(modifier.maxOrMin()) + ")", 100, y,
                0xff000000);
    }

}
