package mcjty.deepresonance.modules.core.block;

import elec332.core.item.AbstractItemBlock;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.util.Constants;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 18-1-2020
 */
public class BlockCrystal extends BaseBlock {

    private static final VoxelShape AABB = VoxelShapes.create(0.1f, 0, 0.1f, 0.9f, 0.8f, 0.9f);

    public BlockCrystal() {
        super(new BlockBuilder()
                .tileEntitySupplier(TileEntityResonatingCrystal::new));
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return AABB;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileEntityResonatingCrystal) {
            return createStack((TileEntityResonatingCrystal) tile);
        }
        return new ItemStack(this);
    }

    public ItemStack createStack(TileEntityResonatingCrystal crystal) {
        ItemStack ret = new ItemStack(this);
        ret.setTagInfo(AbstractItemBlock.TILE_DATA_TAG, crystal.write(new CompoundNBT()));
        return ret;
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        TileEntityResonatingCrystal crystal = new TileEntityResonatingCrystal();
        for (int power : new int[]{0, 50}) {
            for (int purity : new int[]{0, 50}) {
                ItemStack stack = new ItemStack(this);
                crystal.setPurity(purity);
                crystal.setPower(power);
                stack.setTagInfo(AbstractItemBlock.TILE_DATA_TAG, crystal.write(new CompoundNBT()));
                items.add(stack);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null) {
            tagCompound = tagCompound.getCompound(AbstractItemBlock.TILE_DATA_TAG);
        }

        super.addInformation(stack, world, tooltip, advanced);

        float power = 100.0f;
        if (tagCompound != null) {
            power = tagCompound.getFloat("power");
        }
        if (power > Constants.CRYSTAL_MIN_POWER) {
            tooltip.add(new TranslationTextComponent(TranslationHelper.getTooltipKey("crystal_power")));
        } else {
            tooltip.add(new TranslationTextComponent(TranslationHelper.getTooltipKey("crystal_empty")));
        }
        if (tagCompound != null) {
            addBasicInformation(tooltip::add, tagCompound, power, true);
        }
    }

    public static void addBasicInformation(Consumer<ITextComponent> tooltip, CompoundNBT tag, float power, boolean showPower) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        tooltip.accept(new TranslationTextComponent(TranslationHelper.getTooltipKey("crystal_sep"))
                .applyTextStyle(TextFormatting.GREEN)
                .appendText(": "
                        + decimalFormat.format(tag.getFloat("strength")) + "% "
                        + decimalFormat.format(tag.getFloat("efficiency")) + "% "
                        + decimalFormat.format(tag.getFloat("purity")) + "%"
                )
        );
        if (showPower) {
            tooltip.accept(new StringTextComponent("Power left: " + decimalFormat.format(power) + "%").applyTextStyle(TextFormatting.YELLOW));
        }

    }

}
