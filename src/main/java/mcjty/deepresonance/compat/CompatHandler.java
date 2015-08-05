package mcjty.deepresonance.compat;

import cpw.mods.fml.client.CustomModLoadingErrorDisplayException;
import elec332.core.util.AbstractCompatHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 5-8-2015.
 */
public class CompatHandler extends AbstractCompatHandler {

    public CompatHandler(Configuration config, Logger logger) {
        super(config, logger);
    }

    @Override
    public void loadList() {
        RF = isAPILoaded("CoFHAPI|energy");

        checkIfRfLoaded();
    }

    public static boolean RF = false;

    private void checkIfRfLoaded(){
        if (!RF) {
            throw new CustomModLoadingErrorDisplayException("Could not find RF-API, please install a mod that ships it, e.g. CoFH Core, EnderIO, Mekanism, ect", new RuntimeException("Missing API: RF-API")) {
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
            };
        }
    }
}
