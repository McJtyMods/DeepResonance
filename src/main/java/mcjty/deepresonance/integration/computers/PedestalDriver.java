package mcjty.deepresonance.integration.computers;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import mcjty.deepresonance.blocks.pedestal.PedestalTileEntity;
import mcjty.lib.integration.computers.AbstractOCDriver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class PedestalDriver {
    public static class OCDriver extends AbstractOCDriver {
        public OCDriver() {
            super("deepresonance_pedastal", PedestalTileEntity.class);
        }

        public static class InternalManagedEnvironment extends AbstractOCDriver.InternalManagedEnvironment<PedestalTileEntity> {
            public InternalManagedEnvironment(PedestalTileEntity tile) {
                super(tile, "deepresonance_pedastal");
            }

            @Callback(doc="function():boolean; Returns whether there is a crystal on the pedestal.")
            public Object[] crystalPresent(Context c, Arguments a) {
                return new Object[]{tile.crystalPresent()};
            }

            @Callback(doc="function(); Drops the crystal on the pedestal.")
            public Object[] dropCrystal(Context c, Arguments a) {
                tile.dropCrystal();
                return new Object[]{};
            }

            @Callback(doc="function():number; Returns the purity of the crystal on the pedestal.")
            public Object[] getPurity(Context c, Arguments a) {
                Optional<Float> purity = tile.getCrystal().map(cr -> cr.getPurity());
                if (purity.isPresent()) {
                    return new Object[]{purity.get().floatValue()};
                } else {
                    return new Object[]{null, "No crystal present"};
                }
            }

            @Callback(doc="function():number; Returns the strength of the crystal on the pedestal.")
            public Object[] getStrength(Context c, Arguments a) {
                Optional<Float> strength = tile.getCrystal().map(cr -> cr.getStrength());
                if (strength.isPresent()) {
                    return new Object[]{strength.get().floatValue()};
                } else {
                    return new Object[]{null, "No crystal present"};
                }
            }

            @Callback(doc="function():number; Returns the efficiency of the crystal on the pedestal.")
            public Object[] getEfficiency(Context c, Arguments a) {
                Optional<Float> efficiency = tile.getCrystal().map(cr -> cr.getEfficiency());
                if (efficiency.isPresent()) {
                    return new Object[]{efficiency.get().floatValue()};
                } else {
                    return new Object[]{null, "No crystal present"};
                }
            }

            @Callback(doc="function():number; Returns the power left in the crystal on the pedestal.")
            public Object[] getPower(Context c, Arguments a) {
                Optional<Float> power = tile.getCrystal().map(cr -> cr.getPower());
                if (power.isPresent()) {
                    return new Object[]{power.get().floatValue()};
                } else {
                    return new Object[]{null, "No crystal present"};
                }
            }

            @Callback(doc="function():number; Returns the RF output of the crystal on the pedestal in RF/tick.")
            public Object[] getOutput(Context c, Arguments a) {
                Optional<Integer> output = tile.getCrystal().map(cr -> cr.getRfPerTick());
                if (output.isPresent()) {
                    return new Object[]{output.get().intValue()};
                } else {
                    return new Object[]{null, "No crystal present"};
                }
            }

            @Callback(doc="function():number; Returns whether the crystal on the pedestal is currently in use.")
            public Object[] isInUse(Context c, Arguments a) {
                Optional<Boolean> inUse = tile.getCrystal().map(cr -> cr.isGlowing());
                if (inUse.isPresent()) {
                    return new Object[]{inUse.get().booleanValue()};
                } else {
                    return new Object[]{null, "No crystal present"};
                }
            }

            @Override
            public int priority() {
                return 4;
            }
        }

        @Override
        public AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileEntity tile) {
            return new InternalManagedEnvironment((PedestalTileEntity)tile);
        }
    }
}
