package mcjty.deepresonance.client;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 12-8-2015.
 */
public class DRResourceLocation extends ResourceLocation {
    public DRResourceLocation(String location) {
        super(DeepResonance.MODID, location);
    }
}
