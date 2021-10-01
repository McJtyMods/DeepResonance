package mcjty.deepresonance.modules.generator.tile;

import mcjty.deepresonance.modules.generator.GeneratorModule;
import net.minecraft.nbt.CompoundNBT;

public class TileEntityGeneratorController extends AbstractTileEntityGeneratorComponent {

    public TileEntityGeneratorController() {
        super(GeneratorModule.TYPE_GENERATOR_CONTROLLER.get());
    }

    @Override
    public void setPowerInput(int powered) {
        super.setPowerInput(powered);
        if (grid != null) {
            grid.onRedstoneChanged(this.powerLevel > 0);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        tagCompound.putInt("startup", startupTimer);
        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        startupTimer = tagCompound.getInt("startup");
        super.read(tagCompound);
    }

}
