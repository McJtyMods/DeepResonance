package mcjty.deepresonance.modules.tank.blocks;

import com.google.common.base.Preconditions;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.grid.TankGrid;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TankTileEntity extends GenericTileEntity {

    private TankGrid grid;
    private CompoundNBT gridData;

    // Client only
    private Fluid clientRenderFluid;
    private float renderHeight; //Value from 0.0f to 1.0f

    public TankTileEntity() {
        super(TankModule.TYPE_TANK.get());
        gridData = new CompoundNBT();
    }

    public void setClientData(float newHeight, Fluid render) {
        boolean dirty = false;
        if (newHeight >= 0 && renderHeight != newHeight) {
            renderHeight = newHeight;
            dirty = true;
        }
        if (clientRenderFluid != render) {
            clientRenderFluid = render;
            dirty = true;
        }
        if (dirty) {
            this.markDirtyClient();
        }
    }

    // Client side
    public float getClientRenderHeight() {
        return renderHeight;
    }

    // Client side
    public Fluid getClientRenderFluid() {
        return clientRenderFluid;
    }

    public CompoundNBT getGridData() {
        return gridData;
    }

    public void setGridData(CompoundNBT gridData) {
        this.gridData = gridData;
    }

    public void setGrid(TankGrid grid) {
        this.grid = grid;
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        if (grid != null) {
            grid.setDataToTile(this);
        }
        tagCompound.put("grid_data", gridData);
        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        this.gridData = tagCompound.getCompound("grid_data");
        super.read(tagCompound);
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        super.writeClientDataToNBT(tagCompound);
        tagCompound.putFloat("renderC", renderHeight);
        if (clientRenderFluid != null) {
            tagCompound.putString("fluidC", Preconditions.checkNotNull(clientRenderFluid.getRegistryName()).toString());
        }
    }

    @Override
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
        super.readClientDataFromNBT(tagCompound);
        renderHeight = tagCompound.getFloat("renderC");
        if (tagCompound.contains("fluidC")) {
            clientRenderFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tagCompound.getString("fluidC")));
        } else {
            clientRenderFluid = null;
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (FluidUtil.getFluidHandler(player.getItemInHand(hand)).isPresent()) {
            if (!level.isClientSide) {
                FluidUtil.interactWithFluidHandler(player, hand, Preconditions.checkNotNull(getLevel()), result.getBlockPos(), result.getDirection());
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, player, hand, result);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (grid != null) {
            return grid.getCapability(cap, side);
        } else { //Early checks or client
            return super.getCapability(cap, side);
        }
    }

// @todo 1.16
//    @Override
//    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
//        CompoundNBT tag = hitData.getData();
//        if (tag.contains("capacity")) {
//            if (tag.contains("fluid")) {
//                Fluid fluid = RegistryHelper.getFluidRegistry().getValue(new ResourceLocation(tag.getString("fluid")));
//                if (fluid != null) {
//                    information.addInformation(StatCollector.translateToLocal(fluid.getAttributes().getTranslationKey()));
//                    if (tag.contains("efficiency")) {
//                        DecimalFormat decimalFormat = new DecimalFormat("#.#");
//                        decimalFormat.setRoundingMode(RoundingMode.DOWN);
//                        information.addInformation("");
//                        information.addInformation("Efficiency: " + decimalFormat.format(tag.getFloat("efficiency") * 100) + "%");
//                        information.addInformation("Purity: " + decimalFormat.format(tag.getFloat("purity") * 100) + "%");
//                        information.addInformation("Quality: " + decimalFormat.format(tag.getFloat("quality") * 100) + "%");
//                        information.addInformation("Strength: " + decimalFormat.format(tag.getFloat("strength") * 100) + "%");
//                    }
//                }
//            }
//            information.addInformation(tag.getInt("amt") + "/" + tag.getInt("capacity") + "mB");
//        }
//    }

    // @todo 1.16
//    @Override
//    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
//        if (grid != null) {
//            tag.putInt("capacity", grid.getTankCapacity(0));
//            tag.putInt("amt", grid.getFluidAmount());
//            Fluid fluid = grid.getStoredFluid();
//            if (fluid != null) {
//                tag.putString("fluid", Preconditions.checkNotNull(fluid.getRegistryName()).toString());
//                ILiquidCrystalData data = DeepResonanceFluidHelper.readCrystalDataFromStack(grid.getFluidInTank(0));
//                if (data != null) {
//                    tag.putFloat("efficiency", data.getEfficiency());
//                    tag.putFloat("purity", data.getPurity());
//                    tag.putFloat("quality", data.getQuality());
//                    tag.putFloat("strength", data.getStrength());
//                }
//            }
//        }
//    }

}
