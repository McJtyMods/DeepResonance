package mcjty.deepresonance.modules.tank.tile;

import com.google.common.base.Preconditions;
import elec332.core.api.registration.HasSpecialRenderer;
import elec332.core.api.registration.RegisteredTileEntity;
import mcjty.deepresonance.modules.tank.client.TankTESR;
import mcjty.deepresonance.modules.tank.grid.TankGrid;
import mcjty.deepresonance.util.AbstractTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 7-1-2020
 */
@RegisteredTileEntity("tank")
@HasSpecialRenderer(TankTESR.class)
public class TileEntityTank extends AbstractTileEntity {

    private TankGrid grid;
    private CompoundNBT gridData;

    // Client only
    private Fluid clientRenderFluid;
    private float renderHeight; //Value from 0.0f to 1.0f

    public TileEntityTank() {
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

    @OnlyIn(Dist.CLIENT)
    public float getClientRenderHeight() {
        return renderHeight;
    }

    @OnlyIn(Dist.CLIENT)
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
    public CompoundNBT write(CompoundNBT tagCompound) {
        if (grid != null) {
            grid.setDataToTile(this);
        }
        tagCompound.put("grid_data", gridData);
        return super.write(tagCompound);
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
        if (FluidUtil.getFluidHandler(player.getHeldItem(hand)).isPresent()) {
            if (!Preconditions.checkNotNull(getWorld()).isRemote) {
                FluidUtil.interactWithFluidHandler(player, hand, Preconditions.checkNotNull(getWorld()), result.getPos(), result.getFace());
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

}
