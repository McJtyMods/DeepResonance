package mcjty.deepresonance.modules.machines.client;

import mcjty.deepresonance.modules.machines.MachinesModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

public class ClientSetup {
    public static void initClient() {
        ItemBlockRenderTypes.setRenderLayer(MachinesModule.CRYSTALLIZER_BLOCK.get(), RenderType.translucent());
//        RenderTypeLookup.setRenderLayer(MachinesModule.LASER_BLOCK.get(), RenderType.cutout());
    }

//    public static void setupBlockColors() {
//        Minecraft.getInstance().getBlockColors().register((s, world, pos, index) -> {
//            if (index == 1) {
//                TileEntity tile = world.getBlockEntity(pos);
//                if (tile instanceof LaserTileEntity) {
//                    InfusionBonus bonus = ((LaserTileEntity) tile).getActiveBonus();
//                    if (!bonus.isEmpty()) {
//                        return bonus.getColor();
//                    }
//                }
//                return 0x484B52;
//            }
//            return -1;
//        }, MachinesModule.LASER_BLOCK.get());
//        Minecraft.getInstance().getItemColors().register((stack, index) -> index == 1 ? 0x484B52 : -1, MachinesModule.LASER_ITEM.get());
//    }
}
