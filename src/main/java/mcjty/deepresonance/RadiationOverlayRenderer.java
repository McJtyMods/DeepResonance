package mcjty.deepresonance;

import mcjty.deepresonance.items.ModItems;
import mcjty.deepresonance.items.RadiationMonitorItem;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class RadiationOverlayRenderer {

    public static void onRender(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            return;
        }

        if (RadiationConfiguration.radiationOverlayX < 0) {
            return;
        }

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
            return;
        }

        if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() != ModItems.radiationMonitorItem) {
            return;
        }

        RadiationMonitorItem.fetchRadiation(player);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);

        int radiation = new Float(RadiationMonitorItem.radiationStrength).intValue();
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        if (radiation > 0) {
            fontRenderer.drawString(
                    "Radiation: " + radiation,
                    RadiationConfiguration.radiationOverlayX, RadiationConfiguration.radiationOverlayY,
                    RadiationConfiguration.radiationOverlayColor);
        } else {
            fontRenderer.drawString(
                    "No radiation detected",
                    RadiationConfiguration.radiationOverlayX, RadiationConfiguration.radiationOverlayY,
                    RadiationConfiguration.radiationOverlayColorNoRadiation);
        }
    }

}
