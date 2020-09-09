package mcjty.deepresonance.modules.machines.client;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.api.infusion.InfusionBonus;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.tile.TileEntityLaser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.tileentity.TileEntity;

public class ClientSetup {
    public static void initClient() {
        RenderTypeLookup.setRenderLayer(MachinesModule.CRYSTALLIZER_BLOCK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(MachinesModule.LASER_BLOCK.get(), RenderType.getCutout());
    }

    public static void setupBlockColors() {
        Minecraft.getInstance().getBlockColors().register((s, world, pos, index) -> {
            if (index == 1) {
                TileEntity tile = WorldHelper.getTileAt(world, pos);
                if (tile instanceof TileEntityLaser) {
                    InfusionBonus bonus = ((TileEntityLaser) tile).getActiveBonus();
                    if (!bonus.isEmpty()) {
                        return bonus.getColor();
                    }
                }
                return 0x484B52;
            }
            return -1;
        }, MachinesModule.LASER_BLOCK.get());
        Minecraft.getInstance().getItemColors().register((stack, index) -> index == 1 ? 0x484B52 : -1, MachinesModule.LASER_ITEM.get());
    }
}
