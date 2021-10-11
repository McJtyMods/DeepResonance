package mcjty.deepresonance.modules.machines.item;

import mcjty.deepresonance.modules.machines.block.LensBlock;
import net.minecraft.item.BlockItem;

public class ItemLens extends BlockItem {

    public ItemLens(LensBlock block, Properties builder) {
        super(block, builder);
    }

    // @todo 1.16
//    @Override
//    @SuppressWarnings("all")
//    public void afterBlockPlaced(@Nonnull BlockItemUseContext context, @Nonnull BlockState state, @Nonnull TileEntity tile) {
//        ILens lens = new DefaultLens();
//        SubTileLens handler = tile.getCapability(SubTileLens.LENSES).orElse(null);
//        if (handler != null) {
//            handler.addLens(lens, context.getFace().getOpposite());
//        }
//    }
//
//    @Override
//    @SuppressWarnings("all")
//    public void onEmptySolidSideClicked(@Nonnull World world, @Nonnull BlockPos clickedPos, @Nonnull TileEntity tile, @Nonnull Direction hit, PlayerEntity player, ItemStack stack, BlockState state) {
//        ILens lens = new DefaultLens();
//        SubTileLens handler = tile.getCapability(SubTileLens.LENSES).orElse(null);
//        if (handler != null && handler.addLens(lens, hit)) {
//            stack.shrink(1);
//        }
//    }
//
}
