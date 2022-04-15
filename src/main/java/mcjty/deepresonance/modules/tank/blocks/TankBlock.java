package mcjty.deepresonance.modules.tank.blocks;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.fluids.FluidStack;

import static mcjty.lib.builder.TooltipBuilder.*;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

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
        CompoundTag tag = itemStack.getTag();
        if (tag == null) {
            return "";
        }
        CompoundTag infoTag = tag.getCompound("BlockEntityTag").getCompound("Info");
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
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack ret = new ItemStack(this);
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TankTileEntity) {
            ret.addTagElement(CoreModule.TILE_DATA_TAG, tile.saveWithoutMetadata());
        }
        return ret;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof TankTileEntity tank) {
            return tank.getComparatorValue();
        }
        return 0;
    }

}
