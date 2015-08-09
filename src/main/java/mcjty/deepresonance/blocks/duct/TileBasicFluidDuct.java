package mcjty.deepresonance.blocks.duct;

import elec332.core.baseclasses.tileentity.TileBase;
import elec332.core.main.ElecCore;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.grid.fluid.DRFluidDuctGrid;
import mcjty.deepresonance.grid.fluid.event.FluidTileEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class TileBasicFluidDuct extends TileBase {

    public TileBasicFluidDuct(){
        super();
        //ElecCore.Debug = true; //TODO: remove
    }

    public float intTank;

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
        this.intTank = tagCompound.getFloat("amount");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        intTank = getGrid().getFluidShare();
        tagCompound.setFloat("amount", intTank);
    }

    public DRFluidDuctGrid getGrid(){
        return DeepResonance.worldGridRegistry.get(worldObj).getPowerTile(myLocation()).getGrid();
    }

}
