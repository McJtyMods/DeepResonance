package mcjty.deepresonance.integration.computers;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mcjty.deepresonance.blocks.gencontroller.GeneratorControllerTileEntity;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GeneratorControllerDriver {
    public static class OCDriver extends AbstractOCDriver {
        public OCDriver() {
            super("deepresonance_controller", GeneratorControllerTileEntity.class);
        }

        public static class InternalManagedEnvironment extends AbstractOCDriver.InternalManagedEnvironment<GeneratorControllerTileEntity> {
            public InternalManagedEnvironment(GeneratorControllerTileEntity tile) {
                super(tile, "deepresonance_controller");
            }

            @Callback(doc="function(); Activate the generator. This will turn off redstone control.")
            public Object[] activate(Context c, Arguments a) {
                tile.activate();
                return new Object[]{};
            }

            @Callback(doc="function(); Deactivate the generator. Note that this will NOT turn on redstone control.")
            public Object[] deactivate(Context c, Arguments a) {
                tile.deactivate();
                return new Object[]{};
            }

            @Callback(doc="function(bool); Set whether the generator controller is affected by redstone.")
            public Object[] setRSControlled(Context c, Arguments a) {
                tile.setRsControlled(a.checkBoolean(0));
                return new Object[]{};
            }

            @Override
            public int priority() {
                return 4;
            }
        }

        @Override
        public ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileEntity tile) {
            return new InternalManagedEnvironment((GeneratorControllerTileEntity)tile);
        }
    }
}
