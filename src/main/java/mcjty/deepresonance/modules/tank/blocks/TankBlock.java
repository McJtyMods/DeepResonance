package mcjty.deepresonance.modules.tank.blocks;

import net.minecraft.core.Direction;
import mcjty.deepresonance.modules.tank.util.TankTransfers;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.data.TankData;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

import static mcjty.lib.builder.TooltipBuilder.*;

public class TankBlock extends BaseBlock {

    public TankBlock(Properties properties) {
        super(new BlockBuilder()
                .properties(properties.noOcclusion().strength(2.0F).sound(SoundType.GLASS))
                .topDriver(DeepResonanceTOPDriver.DRIVER)
                .tileEntitySupplier(TankTileEntity::new)
                .manualEntry(ManualHelper.create("deepresonance:machines/tank"))
                .info(key(DeepResonance.SHIFT_MESSAGE))
                .infoShift(header(), parameter("liquid", TankBlock::getLiquid)));
    }

    private static String getLiquid(ItemStack itemStack) {
        TankData data = itemStack.get(TankModule.ITEM_TANK_DATA);
        if (data == null || data.preservedLiquid().isEmpty()) {
            return "";
        }
        FluidStack fluidStack = data.preservedLiquid();
        String name = I18n.get(fluidStack.getFluidType().getDescriptionId(fluidStack));
        return name + " (" + fluidStack.getAmount() + "mb)";
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        ItemStack ret = new ItemStack(this);
        BlockEntity tile = level.getBlockEntity(pos);
        if (includeData && tile instanceof TankTileEntity tank) {
            ret.set(TankModule.ITEM_TANK_DATA, new TankData(TankTransfers.contents(tank.getFluidHandler())));
        }
        return ret;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos, Direction direction) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof TankTileEntity tank) {
            return tank.getComparatorValue();
        }
        return 0;
    }

}
