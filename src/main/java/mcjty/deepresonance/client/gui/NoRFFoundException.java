package mcjty.deepresonance.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;

/**
 * Created by Elec332 on 3-10-2015.
 */
public class NoRFFoundException extends CustomModLoadingErrorDisplayException {

    public NoRFFoundException(Exception e){
        super("Could not find RF-API, please install a mod that ships it, e.g. CoFH Core, EnderIO, Mekanism, ect", e);
    }

    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {
    }

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
        int offset = 75;
        errorScreen.drawCenteredString(fontRenderer, "DeepResonance has found a problem with your minecraft installation", errorScreen.width / 2, offset, 0xFFFFFF);
        offset += 15;
        errorScreen.drawCenteredString(fontRenderer, "Could not find RF-API", errorScreen.width / 2, offset, 0xEEEEEE);
        offset += 10;
        errorScreen.drawCenteredString(fontRenderer, "please install a mod that ships it, e.g. CoFH Core, EnderIO, Mekanism, ect", errorScreen.width / 2, offset, 0xEEEEEE);
    }
}
