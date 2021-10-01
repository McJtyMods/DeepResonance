package mcjty.deepresonance.modules.machines.tile;

import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;

public class TileEntityLens extends GenericTileEntity implements ITickableTileEntity {

    public TileEntityLens() {
        super(MachinesModule.TYPE_LENS.get());
    }

    @Override
    public void tick() {
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
    }
}
