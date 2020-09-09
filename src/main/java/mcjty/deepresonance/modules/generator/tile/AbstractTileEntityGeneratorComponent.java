package mcjty.deepresonance.modules.generator.tile;

import elec332.core.api.info.IInfoDataAccessorBlock;
import elec332.core.api.info.IInfoProvider;
import elec332.core.api.info.IInformation;
import elec332.core.util.BlockProperties;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.modules.generator.grid.GeneratorGrid;
import mcjty.deepresonance.util.AbstractTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 1-8-2020
 */
public abstract class AbstractTileEntityGeneratorComponent extends AbstractTileEntity implements IInfoProvider {

    protected GeneratorGrid grid;
    protected int startupTimer = -1;
    protected boolean isOn = false;

    public AbstractTileEntityGeneratorComponent(TileEntityType<?> type) {
        super(type);
    }

    public void setGrid(GeneratorGrid grid) {
        this.grid = grid;
    }

    @Override
    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
        CompoundNBT data = hitData.getData();
        if (data.getBoolean("grid")) {
            if (data.getBoolean("dup")) {
                information.addInformation(TextFormatting.ITALIC + "Multiple controllers or collectors detected!");
            } else {
                information.addInformation(TextFormatting.GREEN + "Energy: " + data.getInt("storedPower") + "/" + data.getInt("maxPower"));
                information.addInformation(TextFormatting.ITALIC + "Status: " + data.getString("startup"));
            }
        }
    }

    @Override
    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
        tag.putBoolean("grid", grid != null);
        if (grid != null) {
            tag.putBoolean("dup", grid.hasDuplicates());
            tag.putInt("maxPower", grid.getMaxEnergyStored());
            tag.putInt("storedPower", grid.getEnergyStored());
            tag.putString("startup", grid.getStartupText());
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putBoolean("isOn", isOn);
        return super.write(tagCompound);
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
        if (WorldHelper.chunkLoaded(getWorld(), getPos())) {
            boolean on = startupTimer >= 0;
            generatorTurnedOn(on);
            markDirtyClient();
        }
    }

    public void generatorTurnedOn(boolean on) {
        if (on != isOn) {
            this.isOn = on;
            BlockState state = WorldHelper.getBlockState(getWorld(), getPos());
            WorldHelper.setBlockState(getWorld(), getPos(), state.with(BlockProperties.ON, on), 3);
        }
    }

}
