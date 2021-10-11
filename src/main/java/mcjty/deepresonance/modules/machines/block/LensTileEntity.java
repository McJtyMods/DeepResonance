package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;

public class LensTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public LensTileEntity() {
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
