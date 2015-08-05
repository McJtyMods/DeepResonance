package mcjty.deepresonance.grid.fluid.event;

import mcjty.deepresonance.grid.fluid.DRWiringTypeHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.WorldEvent;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class FluidTileEvent extends WorldEvent {

    private FluidTileEvent(TileEntity tile){
        super(tile.getWorldObj());
        if (tile.getWorldObj() == null)
            throw new IllegalStateException("Tile tried to fire event but has a null world!?!?");
        if (!DRWiringTypeHelper.instance.isTileValid(tile))
            throw new IllegalArgumentException("Invalid tile: "+tile.getClass().getName());
        this.tile = tile;
    }

    public final TileEntity tile;

    public static class Load extends FluidTileEvent{
        public Load(TileEntity tile) {
            super(tile);
        }
    }

    public static class Unload extends FluidTileEvent{
        public Unload(TileEntity tile) {
            super(tile);
        }
    }
}
