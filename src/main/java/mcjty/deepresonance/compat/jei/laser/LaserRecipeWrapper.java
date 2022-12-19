package mcjty.deepresonance.compat.jei.laser;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.machines.data.InfusingBonus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;

public class LaserRecipeWrapper {

    private final ItemStack item;

    public LaserRecipeWrapper(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void drawInfo(PoseStack stack) {
        InfusingBonus bonus = LaserTileEntity.getInfusingBonus(item);

        renderStat(stack, "Purity:", bonus.purityModifier(), 30);
        renderStat(stack, "Strength:", bonus.strengthModifier(), 40);
        renderStat(stack, "Efficiency:", bonus.efficiencyModifier(), 50);
    }

    private void renderStat(PoseStack stack, String label, InfusingBonus.Modifier modifier, int y) {
        Font fontRenderer = Minecraft.getInstance().font;
        fontRenderer.draw(stack, label, 0, y, 0xffffffff);
        float purityBonus = modifier.bonus();
        fontRenderer.draw(stack, String.valueOf(purityBonus)+"%", 60, y,
                purityBonus > 0 ? 0xff006600 : 0xffff0000);
        fontRenderer.draw(stack, "(" + String.valueOf(modifier.maxOrMin()) + ")", 100, y,
                0xff000000);
    }

}