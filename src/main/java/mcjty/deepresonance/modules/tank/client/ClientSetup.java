package mcjty.deepresonance.modules.tank.client;

import elec332.core.loader.client.RenderingRegistry;
import mcjty.deepresonance.modules.tank.TankModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class ClientSetup {
    public static void initClient() {
        RenderingRegistry.instance().registerLoader(TankRenderer.INSTANCE);
        RenderingRegistry.instance().setItemRenderer(TankModule.TANK_ITEM.get(), new TankItemRenderer());
        RenderTypeLookup.setRenderLayer(TankModule.TANK_BLOCK.get(), RenderType.getTranslucent());
    }
}
