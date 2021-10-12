package mcjty.deepresonance.modules.core.block;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.util.Constants;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
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

public class ResonatingCrystalBlock extends BaseBlock {

    private static final VoxelShape AABB = VoxelShapes.box(0.1f, 0, 0.1f, 0.9f, 0.8f, 0.9f);

    public static BooleanProperty EMPTY = BooleanProperty.create("empty");
    public static BooleanProperty GENERATED = BooleanProperty.create("generated");

    public ResonatingCrystalBlock() {
        super(new BlockBuilder()
                .properties(AbstractBlock.Properties
                        .of(Material.METAL)
                        .strength(2.0f)
                        .sound(SoundType.METAL)
                        .noOcclusion()
                )
                .tileEntitySupplier(ResonatingCrystalTileEntity::new));
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
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof ResonatingCrystalTileEntity) {
            return createStack((ResonatingCrystalTileEntity) tile);
        }
        return new ItemStack(this);
    }

    public ItemStack createStack(ResonatingCrystalTileEntity crystal) {
        ItemStack ret = new ItemStack(this);
        ret.addTagElement(CoreModule.TILE_DATA_TAG, crystal.save(new CompoundNBT()));
        return ret;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        ResonatingCrystalTileEntity crystal = new ResonatingCrystalTileEntity();
        for (int power : new int[]{0, 50}) {
            for (int purity : new int[]{0, 50}) {
                ItemStack stack = new ItemStack(this);
                crystal.setPurity(purity);
                crystal.setPower(power);
                stack.addTagElement(CoreModule.TILE_DATA_TAG, crystal.save(new CompoundNBT()));
                items.add(stack);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null) {
            tagCompound = tagCompound.getCompound(CoreModule.TILE_DATA_TAG);
        }

        super.appendHoverText(stack, world, tooltip, advanced);

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
                .withStyle(TextFormatting.GREEN)
                .append(": "
                        + decimalFormat.format(tag.getFloat("strength")) + "% "
                        + decimalFormat.format(tag.getFloat("efficiency")) + "% "
                        + decimalFormat.format(tag.getFloat("purity")) + "%"
                )
        );
        if (showPower) {
            tooltip.accept(new StringTextComponent("Power left: " + decimalFormat.format(power) + "%").withStyle(TextFormatting.YELLOW));
        }

    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EMPTY, GENERATED);
    }
}
