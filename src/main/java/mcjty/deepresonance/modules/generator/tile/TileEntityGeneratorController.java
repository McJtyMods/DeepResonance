package mcjty.deepresonance.modules.generator.tile;

import elec332.core.api.registration.RegisteredTileEntity;
import net.minecraft.nbt.CompoundNBT;

/**
 * Created by Elec332 on 30-7-2020
 */
@RegisteredTileEntity("generator_controller")
public class TileEntityGeneratorController extends AbstractTileEntityGeneratorComponent {

    @Override
    public void setPowerInput(int powered) {
        super.setPowerInput(powered);
        if (grid != null) {
            grid.onRedstoneChanged(this.powerLevel > 0);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putInt("startup", startupTimer);
        return super.write(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        startupTimer = tagCompound.getInt("startup");
        super.read(tagCompound);
    }

}
