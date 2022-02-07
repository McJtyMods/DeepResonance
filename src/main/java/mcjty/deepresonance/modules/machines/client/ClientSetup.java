package mcjty.deepresonance.modules.machines.client;

import mcjty.deepresonance.modules.machines.MachinesModule;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class ClientSetup {
    public static void initClient() {
        ItemBlockRenderTypes.setRenderLayer(MachinesModule.CRYSTALLIZER_BLOCK.get(), RenderType.translucent());
//        RenderTypeLookup.setRenderLayer(MachinesModule.LASER_BLOCK.get(), RenderType.cutout());
    }
}
