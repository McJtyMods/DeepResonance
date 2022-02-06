package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.lib.tileentity.TickingTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class LensTileEntity extends TickingTileEntity {

    public LensTileEntity(BlockPos pos, BlockState state) {
        super(MachinesModule.TYPE_LENS.get(), pos, state);
    }

    @Override
    public void tickServer() {
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
    }
}
