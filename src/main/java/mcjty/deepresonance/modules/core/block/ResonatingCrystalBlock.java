package mcjty.deepresonance.modules.core.block;

import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.util.Constants;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

public class ResonatingCrystalBlock extends BaseBlock {

    private static final VoxelShape AABB = Shapes.box(0.1f, 0, 0.1f, 0.9f, 0.8f, 0.9f);

    private final boolean generated;
    private final boolean empty;

    public ResonatingCrystalBlock(boolean generated, boolean empty) {
        super(new BlockBuilder()
                .topDriver(DeepResonanceTOPDriver.DRIVER)
                .properties(BlockBehaviour.Properties
                        .of(Material.METAL)
                        .strength(2.0f)
                        .sound(SoundType.METAL)
                        .noOcclusion()
                )
                .tileEntitySupplier(ResonatingCrystalTileEntity::new));
        this.generated = generated;
        this.empty = empty;
    }

    public ResonatingCrystalBlock getEmpty() {
        if (generated) {
            return CoreModule.RESONATING_CRYSTAL_GENERATED_EMPTY.get();
        } else {
            return CoreModule.RESONATING_CRYSTAL_NATURAL_EMPTY.get();
        }
    }

    public boolean isGenerated() {
        return generated;
    }

    public boolean isEmpty() {
        return empty;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return AABB;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof ResonatingCrystalTileEntity) {
            return createStack((ResonatingCrystalTileEntity) tile);
        }
        return new ItemStack(this);
    }

    public ItemStack createStack(ResonatingCrystalTileEntity crystal) {
        ItemStack ret = new ItemStack(this);
        ret.addTagElement(CoreModule.TILE_DATA_TAG, crystal.saveWithoutMetadata());
        return ret;
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        ResonatingCrystalTileEntity crystal = new ResonatingCrystalTileEntity(BlockPos.ZERO, CoreModule.RESONATING_CRYSTAL_GENERATED.get().defaultBlockState());
        for (int power : new int[]{0, 50}) {
            for (int purity : new int[]{0, 50}) {
                ItemStack stack = new ItemStack(this);
                crystal.setPurity(purity);
                crystal.setPower(power);
                stack.addTagElement(CoreModule.TILE_DATA_TAG, crystal.saveWithoutMetadata());
                items.add(stack);
            }
        }
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag advanced) {
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound != null) {
            tagCompound = tagCompound.getCompound(CoreModule.TILE_DATA_TAG).getCompound("Info");
        }

        super.appendHoverText(stack, world, tooltip, advanced);

        float power = 100.0f;
        if (tagCompound != null) {
            power = tagCompound.getFloat("power");
        }
        if (power > Constants.CRYSTAL_MIN_POWER) {
            tooltip.add(new TranslatableComponent("message.deepresonance.crystal_power"));
        } else {
            tooltip.add(new TranslatableComponent("message.deepresonance.crystal_empty"));
        }
        if (tagCompound != null) {
            addBasicInformation(tooltip::add, tagCompound, power, true);
        }
    }

    public static void addBasicInformation(Consumer<Component> tooltip, CompoundTag tag, float power, boolean showPower) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        tooltip.accept(new TranslatableComponent("message.deepresonance.crystal_sep")
                .withStyle(ChatFormatting.GREEN)
                .append(": "
                        + decimalFormat.format(tag.getFloat("strength")) + "% "
                        + decimalFormat.format(tag.getFloat("efficiency")) + "% "
                        + decimalFormat.format(tag.getFloat("purity")) + "%"
                )
        );
        if (showPower) {
            tooltip.accept(new TextComponent("Power left: " + decimalFormat.format(power) + "%").withStyle(ChatFormatting.YELLOW));
        }

    }
}
