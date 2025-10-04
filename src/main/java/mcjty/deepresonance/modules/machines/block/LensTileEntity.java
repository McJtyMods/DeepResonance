package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.lib.tileentity.TickingTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LensTileEntity extends TickingTileEntity {

    public LensTileEntity(BlockPos pos, BlockState state) {
        super(MachinesModule.TYPE_LENS.get(), pos, state);
    }

    @Override
    public void tickServer() {
    }

    // No persistent state yet
}
