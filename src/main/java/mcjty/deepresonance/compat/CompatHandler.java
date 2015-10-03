package mcjty.deepresonance.compat;

import elec332.core.util.AbstractCompatHandler;
import mcjty.deepresonance.DeepResonance;
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
            DeepResonance.proxy.throwException(new RuntimeException("Missing API: RF-API"), 0);
        }
    }
}
