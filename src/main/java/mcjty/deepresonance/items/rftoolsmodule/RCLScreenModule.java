package mcjty.deepresonance.items.rftoolsmodule;

import mcjty.deepresonance.blocks.tank.TankSetup;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import mcjty.lib.varia.BlockPosTools;
import mcjty.rftools.api.screens.IScreenDataHelper;
import mcjty.rftools.api.screens.IScreenModule;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidStack;

public class RCLScreenModule implements IScreenModule<ModuleDataRCL> {
    private int dim = 0;
    private BlockPos coordinate = BlockPosTools.INVALID;

    @Override
    public ModuleDataRCL getData(IScreenDataHelper h, World worldObj, long millis) {
        World world = DimensionManager.getWorld(dim);
        if (world == null) {
            return null;
        }

        if (!world.getChunkProvider().chunkExists(coordinate.getX() >> 4, coordinate.getZ() >> 4)) {
            return null;
        }

        Block block = world.getBlockState(coordinate).getBlock();
        if (block != TankSetup.tank) {
            return null;
        }

        int purity = 0;
        int strength = 0;
        int efficiency = 0;

        TileEntity te = world.getTileEntity(coordinate);
        if (te instanceof TileTank) {
            TileTank tank = (TileTank) te;
            FluidStack fluidStack = tank.drain(null, 1, false);
            if (fluidStack != null) {
                LiquidCrystalFluidTagData data = LiquidCrystalFluidTagData.fromStack(fluidStack);
                if (data != null) {
                    purity = (int) ((data.getPurity() + .005) * 100);
                    strength = (int) ((data.getStrength() + .005) * 100);
                    efficiency = (int) ((data.getEfficiency() + .005) * 100);
                }
            }
        }

        return new ModuleDataRCL(purity, strength, efficiency);
    }

    @Override
    public void setupFromNBT(NBTTagCompound tagCompound, int dim, BlockPos pos) {
        if (tagCompound != null) {
            coordinate = BlockPosTools.INVALID;
            if (tagCompound.hasKey("monitorx")) {
                if (tagCompound.hasKey("monitordim")) {
                    this.dim = tagCompound.getInteger("monitordim");
                } else {
                    // Compatibility reasons
                    this.dim = tagCompound.getInteger("dim");
                }
                if (dim == this.dim) {
                    BlockPos c = new BlockPos(tagCompound.getInteger("monitorx"), tagCompound.getInteger("monitory"), tagCompound.getInteger("monitorz"));
                    int dx = Math.abs(c.getX() - pos.getX());
                    int dy = Math.abs(c.getY() - pos.getY());
                    int dz = Math.abs(c.getZ() - pos.getZ());
                    if (dx <= 64 && dy <= 64 && dz <= 64) {
                        coordinate = c;
                    }
                }
            }
        }
    }

    @Override
    public int getRfPerTick() {
        return RadiationConfiguration.RCLMODULE_RFPERTICK;
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }
}
