package mcjty.deepresonance;

import elec332.core.client.RenderHelper;
import mcjty.deepresonance.items.ModItems;
import mcjty.deepresonance.items.RadiationMonitorItem;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class RadiationOverlayRenderer {

    public static void onRender(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            return;
        }

        if (RadiationConfiguration.radiationOverlayX < 0) {
            return;
        }

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player.getHeldItem(EnumHand.MAIN_HAND) == null) {
            return;
        }

        if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() != ModItems.radiationMonitorItem) {
            return;
        }

        RadiationMonitorItem.fetchRadiation(player);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);

        int radiation = new Float(RadiationMonitorItem.radiationStrength).intValue();
        if (radiation > 0) {
            RenderHelper.getMCFontrenderer().drawString(
                    "Radiation: " + radiation,
                    RadiationConfiguration.radiationOverlayX, RadiationConfiguration.radiationOverlayY,
                    RadiationConfiguration.radiationOverlayColor);
        } else {
            RenderHelper.getMCFontrenderer().drawString(
                    "No radiation detected",
                    RadiationConfiguration.radiationOverlayX, RadiationConfiguration.radiationOverlayY,
                    RadiationConfiguration.radiationOverlayColorNoRadiation);
        }
    }

}
