package mcjty.deepresonance.grid.fluid;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.core.grid.basic.AbstractCableGrid;
import elec332.core.main.ElecCore;
import elec332.core.util.BlockLoc;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRFluidDuctGrid extends AbstractCableGrid<DRFluidDuctGrid, DRFluidTile, DRGridTypeHelper, DRFluidWorldGridHolder> {
    public DRFluidDuctGrid(World world, DRFluidTile p, ForgeDirection direction) {
        super(world, p, direction, DRGridTypeHelper.instance, DeepResonance.worldGridRegistry);
    }

    @Override
    public void onTick() {
        for (BlockLoc loc : locations)
            ElecCore.systemPrintDebug(loc);
    }

    //TODO: private, but I need it now for the debugItem
    public float amount;

    @Override
    protected void invalidate() {
        for (GridData gridData : transmitters)
            WorldHelper.getTileAt(world, gridData.getLoc()).markDirty();
    }

    public void onTileSave(TileEntity tile, NBTTagCompound tagCompound){
        NBTTagCompound gridTag = new NBTTagCompound();
        //internalFluid.writeToNBT(gridTag);
        gridTag.setFloat("IF_amount", amount/transmitters.size());
        tagCompound.setTag("GridTag", gridTag);
    }

    public void onTileLoad(TileEntity tile, NBTTagCompound tagCompound){
        NBTTagCompound gridTag = tagCompound.getCompoundTag("GridTag");
        if (gridTag != null){
            this.amount = gridTag.getFloat("IF_amount");
        }
    }

}
