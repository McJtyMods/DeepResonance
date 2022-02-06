package mcjty.deepresonance.modules.generator.block;

import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.varia.NBTTools;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;

public class GeneratorPartBlock extends BaseBlock {

    public GeneratorPartBlock() {
        super(new BlockBuilder()
                .tileEntitySupplier(GeneratorPartTileEntity::new)
                .topDriver(DeepResonanceTOPDriver.DRIVER)
                .info(key("message.deepresonance.shiftmessage"))
                .infoShift(header(), parameter("power", GeneratorPartBlock::getPowerString)));
    }

    private static String getPowerString(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundTag::getInt, "preserved", 0) + "FE";
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return state.setValue(BlockStateProperties.UP, world.getBlockState(pos.above()).getBlock() == this)
                .setValue(BlockStateProperties.DOWN, world.getBlockState(pos.below()).getBlock() == this)
                .setValue(BlockStateProperties.POWERED, false);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor world, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
        if (facing == Direction.UP) {
            return state.setValue(BlockStateProperties.UP, facingState.getBlock() == this);
        }
        if (facing == Direction.DOWN) {
            return state.setValue(BlockStateProperties.DOWN, facingState.getBlock() == this);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.POWERED, BlockStateProperties.UP, BlockStateProperties.DOWN);
    }

//    @Override
//    public void setPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
//        super.setPlacedBy(world, pos, state, placer, stack);
//        if (!world.isClientSide) {
//
//            TileEntity te = world.getBlockEntity(pos);
//            if (te instanceof GeneratorPartTileEntity) {
//                GeneratorPartTileEntity part = (GeneratorPartTileEntity) te;
//                long energy = stack.hasTag() ? stack.getTag().getLong("energy") : 0;
//                part.setLocalEnergy(energy);
//                part.getNetwork();   // Force a rebuild of the network
//                part.markDirtyQuick();
//            }
//        }
//    }
//

}
