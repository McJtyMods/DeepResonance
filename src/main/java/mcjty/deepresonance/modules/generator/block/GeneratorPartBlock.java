package mcjty.deepresonance.modules.generator.block;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.util.RandomSource;
import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.data.GeneratorPartData;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;

public class GeneratorPartBlock extends BaseBlock {

    public GeneratorPartBlock(Properties properties) {
        super(new BlockBuilder()
                .properties(properties)
                .tileEntitySupplier(GeneratorPartTileEntity::new)
                .topDriver(DeepResonanceTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("deepresonance:generator/generator"))
                .info(key("message.deepresonance.shiftmessage"))
                .infoShift(header(), parameter("power", GeneratorPartBlock::getPowerString)));
        registerDefaultState(defaultBlockState()
                .setValue(BlockStateProperties.POWERED, false)
                .setValue(BlockStateProperties.UP, false)
                .setValue(BlockStateProperties.DOWN, false));
    }

    private static String getPowerString(ItemStack stack) {
        GeneratorPartData data = stack.getOrDefault(GeneratorModule.ITEM_GENERATOR_PART_DATA, GeneratorPartData.DEFAULT);
        return data.preservedEnergy() + "FE";
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
                .setValue(BlockStateProperties.DOWN, world.getBlockState(pos.below()).getBlock() == this);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack tool,
                                       boolean willHarvest, FluidState fluid) {
        boolean removed = super.onDestroyedByPlayer(state, level, pos, player, tool, willHarvest, fluid);
        if (removed && player instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
            // Confirm the removal before the prediction is acknowledged, even if chunk updates are deferred.
            serverPlayer.connection.send(new ClientboundBlockUpdatePacket(level, pos));
        }
        return removed;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
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
//        if (!world.isClientSide()) {
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
