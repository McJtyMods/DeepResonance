package mcjty.deepresonance.modules.tank.blocks;

import elec332.core.item.AbstractItemBlock;
import elec332.core.util.StatCollector;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.tank.tile.TileEntityTank;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 7-1-2020
 */
public class BlockTank extends BaseBlock {

    public BlockTank() {
        super(new BlockBuilder()
                .properties(Properties.create(Material.IRON).notSolid().hardnessAndResistance(2.0F).sound(SoundType.METAL))
                .tileEntitySupplier(TileEntityTank::new)
                .info(TooltipBuilder.key(DeepResonance.SHIFT_MESSAGE))
                .infoShift(TooltipBuilder.key(TranslationHelper.getExtendedTooltipKey("tank")), TooltipBuilder.parameter("", itemStack -> {
                    FluidStack stack = readFromTileNbt(AbstractItemBlock.getTileData(itemStack));
                    if (!stack.isEmpty()) {
                        return TextFormatting.GREEN + "Fluid: " + StatCollector.translateToLocal(stack.getFluid().getAttributes().getTranslationKey(stack))
                                + "\n" + TextFormatting.GREEN + "Amount: " + stack.getAmount() + " mb";
                    }
                    return "";
                })));
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE; //No rotations 4 u
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack ret = new ItemStack(this);
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileEntityTank) {
            ret.setTagInfo(AbstractItemBlock.TILE_DATA_TAG, tile.write(new CompoundNBT()));
        }
        return ret;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideInvisible(@Nonnull BlockState state, BlockState adjacentBlockState, @Nonnull Direction side) {
        return adjacentBlockState.getBlock() == this;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorInputOverride(@Nonnull BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(@Nonnull BlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(worldIn, pos);
        if (tile instanceof TileEntityTank) {
            return 0;
        }
        return super.getComparatorInputOverride(blockState, worldIn, pos);
    }

    public static FluidStack readFromTileNbt(CompoundNBT tag) {
        if (tag == null || !tag.contains("grid_data")) {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(tag.getCompound("grid_data").getCompound("fluid"));
    }

}
