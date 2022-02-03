package mcjty.deepresonance.modules.radiation.client;

import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class RadiationOverlayRenderer {

    public static void onRender(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            return;
        }

        if (RadiationConfiguration.RADIATION_OVERLAY_X.get() < 0) {
            return;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        ItemStack heldItem = player.getItemBySlot(EquipmentSlotType.MAINHAND);
        if (heldItem.getItem() != RadiationModule.RADIATION_MONITOR.get()) {
            return;
        }

        RadiationMonitorItem.fetchRadiation(player);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);

        int radiation = new Float(RadiationMonitorItem.radiationStrength).intValue();
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        if (radiation > 0) {
            fontRenderer.draw(event.getMatrixStack(),
                    "Radiation: " + radiation,
                    RadiationConfiguration.RADIATION_OVERLAY_X.get(), RadiationConfiguration.RADIATION_OVERLAY_Y.get(),
                    RadiationConfiguration.RADIATION_OVERLAY_COLOR.get());
        } else {
            fontRenderer.draw(
                    event.getMatrixStack(),
                    "No radiation detected",
                    RadiationConfiguration.RADIATION_OVERLAY_X.get(), RadiationConfiguration.RADIATION_OVERLAY_Y.get(),
                    RadiationConfiguration.RADIATION_OVERLAY_COLOR_NORADIATION.get());
        }
    }

}
