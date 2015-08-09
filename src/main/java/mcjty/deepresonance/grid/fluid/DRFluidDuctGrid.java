package mcjty.deepresonance.grid.fluid;

import elec332.core.grid.basic.AbstractCableGrid;
import elec332.core.main.ElecCore;
import elec332.core.player.PlayerHelper;
import elec332.core.server.ServerHelper;
import elec332.core.util.BlockLoc;
import elec332.core.util.NBTHelper;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.duct.TileBasicFluidDuct;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRFluidDuctGrid extends AbstractCableGrid<DRFluidDuctGrid, DRFluidTile, DRGridTypeHelper, DRFluidWorldGridHolder> {
    public DRFluidDuctGrid(World world, DRFluidTile p, ForgeDirection direction) {
        super(world, p, direction, DRGridTypeHelper.instance, DeepResonance.worldGridRegistry);
        if (p.getTile() instanceof TileBasicFluidDuct)
            amount += ((TileBasicFluidDuct) p.getTile()).intTank;
    }

    @Override
    protected void uponGridMerge(DRFluidDuctGrid grid) {
        super.uponGridMerge(grid);
        amount += grid.amount;
    }

    @Override
    public void onTick() {
        for (BlockLoc loc : locations)
            ElecCore.systemPrintDebug(loc);
    }

    @Override
    protected void onTileRemoved(DRFluidTile tile) {
        super.onTileRemoved(tile);
        for (GridData gridData : transmitters){
            TileEntity tileEntity = WorldHelper.getTileAt(world, gridData.getLoc());
            if (tileEntity == null)
                tileEntity = tile.getTile();
            ((TileBasicFluidDuct) tileEntity).intTank = getFluidShare();
        }
        amount -= ((TileBasicFluidDuct)tile.getTile()).intTank;
    }

    //TODO: private, but I need it now for the debugItem
    public float amount;

    public float getFluidShare(){
        return amount/transmitters.size();
    }

    public void unloadTile(TileEntity tile){
        BlockLoc loc = new BlockLoc(tile);
        for (GridData gridData : transmitters){
            if (!gridData.getLoc().equals(loc))
                WorldHelper.getTileAt(world, gridData.getLoc()).markDirty();
        }
    }

    protected NBTTagCompound createTileTag(TileEntity tile){
        //tile.markDirty();
        message("NewTag created");
        return new NBTHelper().addToTag(amount/transmitters.size(), "amount").toNBT();
    }

    protected void readTileTag(NBTTagCompound tagCompound){
        message("Read tag");
        if (tagCompound != null) {
            message("Read tag: ./");
            amount += tagCompound.getFloat("amount");
        }
    }

    public void test(){
        invalidate();
    }

    public static void message(String s){
        try {
            PlayerHelper.sendMessageToPlayer(ServerHelper.instance.getOnlinePlayers().get(0), s);
        } catch (Exception e){
            //for (int i = 0; i < 100; i++) {
               System.out.println(s);
            //}
        }
    }

}
