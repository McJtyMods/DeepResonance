package mcjty.deepresonance.integration.computers;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import mcjty.deepresonance.blocks.valve.ValveTileEntity;
import mcjty.lib.integration.computers.AbstractOCDriver;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ValveDriver {
    public static class OCDriver extends AbstractOCDriver {
        public OCDriver() {
            super("deepresonance_valve", ValveTileEntity.class);
        }

        public static class InternalManagedEnvironment extends AbstractOCDriver.InternalManagedEnvironment<ValveTileEntity> {
            public InternalManagedEnvironment(ValveTileEntity tile) {
                super(tile, "deepresonance_valve");
            }

            @Callback(doc="function():number; Get the minimum purity setting")
            public Object[] getMinPurity(Context c, Arguments a) {
                return new Object[]{tile.getMinPurity() * 100.0f};
            }

            @Callback(doc="function(number); Set the minimum purity setting")
            public Object[] setMinPurity(Context c, Arguments a) {
                double newVal = a.checkDouble(0);
                if (newVal < 0.0f || newVal > 100.0f) {
                    return new Object[]{false, "Value needs to be between 0 and 100"};
                }
                tile.setMinPurity((float)newVal / 100.0f);
                tile.markDirtyClient();
                return new Object[]{true};
            }

            @Callback(doc="function():number; Get the minimum strength setting")
            public Object[] getMinStrength(Context c, Arguments a) {
                return new Object[]{tile.getMinStrength() * 100.0f};
            }

            @Callback(doc="function(number); Set the minimum strength setting")
            public Object[] setMinStrength(Context c, Arguments a) {
                double newVal = a.checkDouble(0);
                if (newVal < 0.0f || newVal > 100.0f) {
                    return new Object[]{false, "Value needs to be between 0 and 100"};
                }
                tile.setMinStrength((float)newVal / 100.0f);
                tile.markDirtyClient();
                return new Object[]{true};
            }

            @Callback(doc="function():number; Get the minimum efficiency setting")
            public Object[] getMinEfficiency(Context c, Arguments a) {
                return new Object[]{tile.getMinEfficiency() * 100.0f};
            }

            @Callback(doc="function(number); Set the minimum efficiency setting")
            public Object[] setMinEfficiency(Context c, Arguments a) {
                double newVal = a.checkDouble(0);
                if (newVal < 0.0f || newVal > 100.0f) {
                    return new Object[]{false, "Value needs to be between 0 and 100"};
                }
                tile.setMinEfficiency((float)newVal / 100.0f);
                tile.markDirtyClient();
                return new Object[]{true};
            }

            @Callback(doc="function():number; Get the maximum amount of fluid")
            public Object[] getMaxMb(Context c, Arguments a) {
                return new Object[]{tile.getMaxMb()};
            }

            @Callback(doc="function(number); Set the maximum amount of fluid")
            public Object[] setMaxMb(Context c, Arguments a) {
                double newVal = a.checkDouble(0);
                if (newVal < 0.0f) {
                    return new Object[]{false, "Value needs to be greater than or equal to 0"};
                }
                tile.setMaxMb((int)newVal);
                tile.markDirtyClient();
                return new Object[]{true};
            }

            @Callback(doc="function():string; Get the current redstone mode. One of \"Ignored\", \"Off\" and \"On\"")
            public Object[] getRedstoneMode(Context c, Arguments a) {
                return new Object[]{tile.getRSMode().getDescription()};
            }

            @Callback(doc="function(string); Set the redstone mode. One of \"Ignored\", \"Off\" and \"On\"")
            public Object[] setRedstoneMode(Context c, Arguments a) {
                String newVal = a.checkString(0);
                RedstoneMode rsMode = RedstoneMode.getMode(newVal);
                if (rsMode != null) {
                    tile.setRSMode(rsMode);
                    tile.markDirtyClient();
                    return new Object[]{true};
                } else {
                    return new Object[]{false, "Not a valid redstone mode. Needs to be one of \"Ignored\", \"Off\" and \"On\""};
                }
            }

            @Override
            public int priority() {
                return 4;
            }
        }

        @Override
        public AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileEntity tile) {
            return new InternalManagedEnvironment((ValveTileEntity)tile);
        }
    }
}
