package mcjty.deepresonance.modules.machines.item;

import mcjty.deepresonance.api.laser.ILens;
import mcjty.deepresonance.modules.machines.tile.SubTileLens;
import mcjty.deepresonance.modules.machines.util.DefaultLens;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemLens extends BlockItem {

    public ItemLens(BlockSubTile block, Properties builder) {
        super(block, builder);
    }

    @Override
    @SuppressWarnings("all")
    public void afterBlockPlaced(@Nonnull BlockItemUseContext context, @Nonnull BlockState state, @Nonnull TileEntity tile) {
        ILens lens = new DefaultLens();
        SubTileLens handler = tile.getCapability(SubTileLens.LENSES).orElse(null);
        if (handler != null) {
            handler.addLens(lens, context.getFace().getOpposite());
        }
    }

    @Override
    @SuppressWarnings("all")
    public void onEmptySolidSideClicked(@Nonnull World world, @Nonnull BlockPos clickedPos, @Nonnull TileEntity tile, @Nonnull Direction hit, PlayerEntity player, ItemStack stack, BlockState state) {
        ILens lens = new DefaultLens();
        SubTileLens handler = tile.getCapability(SubTileLens.LENSES).orElse(null);
        if (handler != null && handler.addLens(lens, hit)) {
            stack.shrink(1);
        }
    }

}
