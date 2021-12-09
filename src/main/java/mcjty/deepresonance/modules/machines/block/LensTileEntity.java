package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.lib.tileentity.TickingTileEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class LensTileEntity extends TickingTileEntity {

    public LensTileEntity() {
        super(MachinesModule.TYPE_LENS.get());
    }

    @Override
    public void tickServer() {
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
