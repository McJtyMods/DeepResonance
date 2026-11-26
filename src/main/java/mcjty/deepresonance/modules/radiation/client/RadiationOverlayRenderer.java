package mcjty.deepresonance.modules.radiation.client;

import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;

public class RadiationOverlayRenderer {

    public static void onRender(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        if (RadiationConfiguration.RADIATION_OVERLAY_X.get() < 0) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack heldItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (heldItem.getItem() != RadiationModule.RADIATION_MONITOR.get()) {
            return;
        }

        RadiationMonitorItem.fetchRadiation(player);

        int radiation = (int) RadiationMonitorItem.radiationStrength;
        Font fontRenderer = Minecraft.getInstance().font;
        if (radiation > 0) {
            graphics.text(fontRenderer,
                    "Radiation: " + radiation,
                    RadiationConfiguration.RADIATION_OVERLAY_X.get(), RadiationConfiguration.RADIATION_OVERLAY_Y.get(),
                    RadiationConfiguration.RADIATION_OVERLAY_COLOR.get());
        } else {
            graphics.text(fontRenderer,
                    "No radiation detected",
                    RadiationConfiguration.RADIATION_OVERLAY_X.get(), RadiationConfiguration.RADIATION_OVERLAY_Y.get(),
                    RadiationConfiguration.RADIATION_OVERLAY_COLOR_NORADIATION.get());
        }
    }

}
