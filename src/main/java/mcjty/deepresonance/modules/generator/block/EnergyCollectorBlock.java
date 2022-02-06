package mcjty.deepresonance.modules.generator.block;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nonnull;

public class EnergyCollectorBlock extends BaseBlock {

    private static final VoxelShape AABB = Shapes.box(0, 0, 0, 1, 5 / 16f, 1);

    public EnergyCollectorBlock() {
        super(new BlockBuilder()
                .tileEntitySupplier(EnergyCollectorTileEntity::new)
                .info(TooltipBuilder.key("message.deepresonance.shiftmessage"))
                .infoShift(TooltipBuilder.header()));
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return AABB;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

}
