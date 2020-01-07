package mcjty.deepresonance.blocks;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.tile.TileEntityTank;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return super.getComparatorInputOverride(blockState, worldIn, pos);
    }

}
