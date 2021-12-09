package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;

import javax.annotation.Nonnull;

public class LensTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public LensTileEntity() {
        super(MachinesModule.TYPE_LENS.get());
    }

    @Override
    public void tick() {
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        super.saveAdditional(tagCompound);
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        super.load(tagCompound);
    }
}
