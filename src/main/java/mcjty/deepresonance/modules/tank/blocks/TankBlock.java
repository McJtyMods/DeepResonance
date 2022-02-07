package mcjty.deepresonance.modules.tank.blocks;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fluids.FluidStack;

import static mcjty.lib.builder.TooltipBuilder.*;

public class TankBlock extends BaseBlock {

    public TankBlock() {
        super(new BlockBuilder()
                .properties(Properties.of(Material.METAL).noOcclusion().strength(2.0F).sound(SoundType.GLASS))
                .topDriver(DeepResonanceTOPDriver.DRIVER)
                .tileEntitySupplier(TankTileEntity::new)
                .info(key(DeepResonance.SHIFT_MESSAGE))
                .infoShift(header(), parameter("liquid", TankBlock::getLiquid)));
    }

    private static String getLiquid(ItemStack itemStack) {
        CompoundNBT tag = itemStack.getTag();
        if (tag == null) {
            return "";
        }
        CompoundNBT infoTag = tag.getCompound("BlockEntityTag").getCompound("Info");
        if (infoTag.contains("preserved")) {
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(infoTag.getCompound("preserved"));
            if (!fluidStack.isEmpty()) {
                String name = I18n.get(fluidStack.getTranslationKey());
                return name + " (" + fluidStack.getAmount() + "mb)";
            }
        }
        return "";
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack ret = new ItemStack(this);
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TankTileEntity) {
            ret.addTagElement(CoreModule.TILE_DATA_TAG, tile.save(new CompoundNBT()));
        }
        return ret;
    }

//@todo 1.16
//    @Override
//    @SuppressWarnings("deprecation")
//    public boolean hasComparatorInputOverride(@Nonnull BlockState state) {
//        return true;
//    }
//
//    @Override
//    @SuppressWarnings("deprecation")
//    public int getComparatorInputOverride(@Nonnull BlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
//        TileEntity tile = WorldHelper.getTileAt(worldIn, pos);
//        if (tile instanceof TileEntityTank) {
//            return 0;
//        }
//        return super.getComparatorInputOverride(blockState, worldIn, pos);
//    }

}
