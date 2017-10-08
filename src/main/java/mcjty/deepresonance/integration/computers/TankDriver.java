package mcjty.deepresonance.integration.computers;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.lib.integration.computers.AbstractOCDriver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TankDriver {
    public static class OCDriver extends AbstractOCDriver {
        public OCDriver() {
            super("deepresonance_tank", TileTank.class);
        }

        public static class InternalManagedEnvironment extends AbstractOCDriver.InternalManagedEnvironment<TileTank> {
            public InternalManagedEnvironment(TileTank tile) {
                super(tile, "deepresonance_tank");
            }

			@Callback(doc="function():number; Returns the maximum amount of fluid")
			public Object[] getCapacity(Context c, Arguments a) {
			    return new Object[]{tile.getCapacity()};
            }

            @Callback(doc="function():number; Returns the current amount of fluid")
            public Object[] getFluidAmount(Context c, Arguments a) {
                return new Object[]{tile.getFluidAmount()};
            }

            @Callback(doc="function():number; Returns the quality of the RCL")
            public Object[] getQuality(Context c, Arguments a) {
                NBTTagCompound tagData = tile.getFluidTag();
                if (tagData == null) {
                    return new Object[]{-1.0f};
                }
                return new Object[]{tagData.getFloat("quality")};
            }

            @Callback(doc="function():number; Returns the purity of the RCL")
            public Object[] getPurity(Context c, Arguments a) {
                NBTTagCompound tagData = tile.getFluidTag();
                if (tagData == null) {
                    return new Object[]{-1.0f};
                }
                return new Object[]{tagData.getFloat("purity")};
            }

            @Callback(doc="function():number; Returns the strength of the RCL")
            public Object[] getStrength(Context c, Arguments a) {
                NBTTagCompound tagData = tile.getFluidTag();
                if (tagData == null) {
                    return new Object[]{-1.0f};
                }
                return new Object[]{tagData.getFloat("strength")};
            }

            @Callback(doc="function():number; Returns the efficiency of the RCL")
            public Object[] getEfficiency(Context c, Arguments a) {
                NBTTagCompound tagData = tile.getFluidTag();
                if (tagData == null) {
                    return new Object[]{-1.0f};
                }
                return new Object[]{tagData.getFloat("efficiency")};
            }

            @Override
            public int priority() {
                return 4;
            }
        }

        @Override
        public AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileEntity tile) {
            return new InternalManagedEnvironment((TileTank)tile);
        }
    }
}
