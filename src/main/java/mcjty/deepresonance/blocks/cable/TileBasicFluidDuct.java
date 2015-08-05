package mcjty.deepresonance.blocks.cable;

import elec332.core.baseclasses.tileentity.TileBase;
import elec332.core.main.ElecCore;
import mcjty.deepresonance.grid.fluid.event.FluidTileEvent;
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

}
