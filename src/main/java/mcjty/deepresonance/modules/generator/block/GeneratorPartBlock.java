package mcjty.deepresonance.modules.generator.block;

import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.NBTTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

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
        return NBTTools.getInfoNBT(stack, CompoundNBT::getInt, "preserved", 0) + "FE";
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return state.setValue(BlockStateProperties.UP, world.getBlockState(pos.above()).getBlock() == this)
                .setValue(BlockStateProperties.DOWN, world.getBlockState(pos.below()).getBlock() == this)
                .setValue(BlockStateProperties.POWERED, false);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull IWorld world, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
        if (facing == Direction.UP) {
            return state.setValue(BlockStateProperties.UP, facingState.getBlock() == this);
        }
        if (facing == Direction.DOWN) {
            return state.setValue(BlockStateProperties.DOWN, facingState.getBlock() == this);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
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
