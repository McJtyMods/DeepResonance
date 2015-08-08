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
        ElecCore.Debug = true; //TODO: remove
    }

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
    public void readFromNBT(final NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        if (!worldObj.isRemote) {
            ElecCore.tickHandler.registerCall(new Runnable() {
                @Override
                public void run() {
                    getGrid().onTileLoad(TileBasicFluidDuct.this, tagCompound);
                }
            });
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        getGrid().onTileSave(this, tagCompound);
    }

    public DRFluidDuctGrid getGrid(){
        return DeepResonance.worldGridRegistry.get(worldObj).getPowerTile(myLocation()).getGrid();
    }

}
