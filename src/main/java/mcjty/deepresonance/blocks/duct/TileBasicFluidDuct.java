package mcjty.deepresonance.blocks.duct;

import elec332.core.baseclasses.tileentity.TileBase;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.grid.fluid.DRFluidDuctGrid;
import mcjty.deepresonance.grid.fluid.event.FluidTileEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class TileBasicFluidDuct extends TileBase {

    public FluidStack intTank;

    @Override
    public void onTileLoaded() {
        super.onTileLoaded();
        if (!worldObj.isRemote)
            MinecraftForge.EVENT_BUS.post(new FluidTileEvent.Load(this));
    }

    @Override
    public void onTileUnloaded() {
        super.onTileUnloaded();
        if (!worldObj.isRemote)
            MinecraftForge.EVENT_BUS.post(new FluidTileEvent.Unload(this));
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        this.intTank = FluidStack.loadFluidStackFromNBT(tagCompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        intTank = getGrid().getFluidShare(this);
        intTank.writeToNBT(tagCompound);
    }

    public int getTankStorageMax(){
        return 200;
    }

    public DRFluidDuctGrid getGrid(){
        if (!worldObj.isRemote)
            return DeepResonance.worldGridRegistry.get(worldObj).getPowerTile(myLocation()).getGrid();
        return null;
    }

}
