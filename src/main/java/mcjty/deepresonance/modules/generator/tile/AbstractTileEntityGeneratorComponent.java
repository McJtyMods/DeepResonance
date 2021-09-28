package mcjty.deepresonance.modules.generator.tile;

import mcjty.deepresonance.modules.generator.grid.GeneratorGrid;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.WorldTools;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

/**
 * Created by Elec332 on 1-8-2020
 */
public abstract class AbstractTileEntityGeneratorComponent extends GenericTileEntity {

    protected GeneratorGrid grid;
    protected int startupTimer = -1;
    protected boolean isOn = false;

    public AbstractTileEntityGeneratorComponent(TileEntityType<?> type) {
        super(type);
    }

    public void setGrid(GeneratorGrid grid) {
        this.grid = grid;
    }

    // @todo 1.16
//
//    @Override
//    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
//        CompoundNBT data = hitData.getData();
//        if (data.getBoolean("grid")) {
//            if (data.getBoolean("dup")) {
//                information.addInformation(TextFormatting.ITALIC + "Multiple controllers or collectors detected!");
//            } else {
//                information.addInformation(TextFormatting.GREEN + "Energy: " + data.getInt("storedPower") + "/" + data.getInt("maxPower"));
//                information.addInformation(TextFormatting.ITALIC + "Status: " + data.getString("startup"));
//            }
//        }
//    }

    // @todo 1.16
//    @Override
//    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
//        tag.putBoolean("grid", grid != null);
//        if (grid != null) {
//            tag.putBoolean("dup", grid.hasDuplicates());
//            tag.putInt("maxPower", grid.getMaxEnergyStored());
//            tag.putInt("storedPower", grid.getEnergyStored());
//            tag.putString("startup", grid.getStartupText());
//        }
//    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        tagCompound.putBoolean("isOn", isOn);
        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        isOn = tagCompound.getBoolean("isOn");
        super.read(tagCompound);
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        super.writeClientDataToNBT(tagCompound);
        tagCompound.putInt("startup", startupTimer);
    }

    @Override
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
        super.readClientDataFromNBT(tagCompound);
        startupTimer = tagCompound.getInt("startup");
    }

    public int getStartupTimer() {
        return startupTimer;
    }

    public void setStartupTimer(int startupTimer) {
        this.startupTimer = startupTimer;
        if (WorldTools.isLoaded(getLevel(), getBlockPos())) {
            boolean on = startupTimer >= 0;
            generatorTurnedOn(on);
            markDirtyClient();
        }
    }

    public void generatorTurnedOn(boolean on) {
        if (on != isOn) {
            this.isOn = on;
            BlockState state = level.getBlockState(getBlockPos());
            level.setBlock(getBlockPos(), state.setValue(BlockStateProperties.POWERED, on), Constants.BlockFlags.DEFAULT);
        }
    }

}
