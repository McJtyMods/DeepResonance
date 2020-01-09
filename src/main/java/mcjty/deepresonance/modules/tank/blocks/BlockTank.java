package mcjty.deepresonance.modules.tank.blocks;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.tank.tile.TileEntityTank;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 7-1-2020
 */
public class BlockTank extends BaseBlock {

    public BlockTank() {
        super(new BlockBuilder()
                .tileEntitySupplier(TileEntityTank::new)
                .info(DeepResonance.SHIFT_MESSAGE)
                .infoExtended("tooltip.deepresonance.tank_extended")
                .infoParameter(itemStack -> {
                    CompoundNBT tagCompound = itemStack.getTag();
                    if (tagCompound != null) {
//                        FluidStack fluidStack = TileTank.getFluidStackFromNBT(tagCompound);
//                        if (fluidStack != null) {
//                            return TextFormatting.GREEN + "Fluid: " + DRFluidRegistry.getFluidName(fluidStack)
//                                    + "\n" +
//                                    TextFormatting.GREEN + "Amount: " + DRFluidRegistry.getAmount(fluidStack) + " mb";
//                        }
                    }
                    return "";
                }));
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE; //No rotations 4 u
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.getBlock() == this;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(worldIn, pos);
        if (tile instanceof TileEntityTank) {
            return 0;
        }
        return super.getComparatorInputOverride(blockState, worldIn, pos);
    }

}
