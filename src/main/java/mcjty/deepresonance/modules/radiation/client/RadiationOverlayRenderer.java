package mcjty.deepresonance.modules.radiation.client;

import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;

public class RadiationOverlayRenderer {

    public static void onRender(RenderGuiOverlayEvent event) {
        if (event.isCancelable() || event.getOverlay() != VanillaGuiOverlay.BOSS_EVENT_PROGRESS.type()) {
            return;
        }

        if (RadiationConfiguration.RADIATION_OVERLAY_X.get() < 0) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        ItemStack heldItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (heldItem.getItem() != RadiationModule.RADIATION_MONITOR.get()) {
            return;
        }

        RadiationMonitorItem.fetchRadiation(player);

        int radiation = (int) RadiationMonitorItem.radiationStrength;
        Font fontRenderer = Minecraft.getInstance().font;
        if (radiation > 0) {
            event.getGuiGraphics().drawString(fontRenderer,
                    "Radiation: " + radiation,
                    RadiationConfiguration.RADIATION_OVERLAY_X.get(), RadiationConfiguration.RADIATION_OVERLAY_Y.get(),
                    RadiationConfiguration.RADIATION_OVERLAY_COLOR.get());
        } else {
            event.getGuiGraphics().drawString(fontRenderer,
                    "No radiation detected",
                    RadiationConfiguration.RADIATION_OVERLAY_X.get(), RadiationConfiguration.RADIATION_OVERLAY_Y.get(),
                    RadiationConfiguration.RADIATION_OVERLAY_COLOR_NORADIATION.get());
        }
    }

}
