package mcjty.deepresonance.modules.tank.client;

import mcjty.deepresonance.modules.tank.TankModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

public class ClientSetup {
    public static void initClient() {
        // @todo 1.16
//        RenderingRegistry.instance().registerLoader(TankRenderer.INSTANCE);
//        RenderingRegistry.instance().setItemRenderer(TankModule.TANK_ITEM.get(), new TankItemRenderer());
        ItemBlockRenderTypes.setRenderLayer(TankModule.TANK_BLOCK.get(), RenderType.translucent());
    }
}
