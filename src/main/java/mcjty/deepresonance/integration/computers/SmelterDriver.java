package mcjty.deepresonance.integration.computers;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import mcjty.deepresonance.blocks.smelter.SmelterTileEntity;
import mcjty.lib.integration.computers.AbstractOCDriver;
import mcjty.lib.network.Argument;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class SmelterDriver {
    public static class OCDriver extends AbstractOCDriver {
        public OCDriver() {
            super("deepresonance_smelter", SmelterTileEntity.class);
        }

        public static class InternalManagedEnvironment extends AbstractOCDriver.InternalManagedEnvironment<SmelterTileEntity> {
            public InternalManagedEnvironment(SmelterTileEntity tile) {
                super(tile, "deepresonance_smelter");
            }

            @Callback(doc="function():number; Get the currently stored energy")
            public Object[] getEnergy(Context c, Arguments a) {
                return new Object[]{tile.getEnergyStored(EnumFacing.DOWN)};
            }

            @Callback(doc="function():number; Get the maximum stored energy")
            public Object[] getMaxEnergy(Context c, Arguments a) {
                return new Object[]{tile.getMaxEnergyStored(EnumFacing.DOWN)};
            }

            @Callback(doc="function():number; Get the current progress in percent")
            public Object[] getProgress(Context c, Arguments a) {
                Integer progress = tile.executeWithResultInteger(SmelterTileEntity.CMD_GETPROGRESS, new HashMap<String, Argument>());
                return new Object[]{progress.intValue()};
            }

            @Override
            public int priority() {
                return 4;
            }
        }

        @Override
        public ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileEntity tile) {
            return new InternalManagedEnvironment((SmelterTileEntity)tile);
        }
    }
}
