package mcjty.deepresonance.modules.tank.blocks;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.util.ItemDataHelper;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.FluidStack;

import static mcjty.lib.builder.TooltipBuilder.*;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class TankBlock extends BaseBlock {

    private static final HolderLookup.Provider BUILTIN_PROVIDER = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY).freeze();

    public TankBlock() {
        super(new BlockBuilder()
                .properties(Properties.of().noOcclusion().strength(2.0F).sound(SoundType.GLASS))
                .topDriver(DeepResonanceTOPDriver.DRIVER)
                .tileEntitySupplier(TankTileEntity::new)
                .info(key(DeepResonance.SHIFT_MESSAGE))
                .infoShift(header(), parameter("liquid", TankBlock::getLiquid)));
    }

    private static String getLiquid(ItemStack itemStack) {
        CompoundTag infoTag = ItemDataHelper.getInfoTag(itemStack);
        if (infoTag == null || !infoTag.contains("preserved")) {
            return "";
        }
        FluidStack fluidStack = FluidStack.parseOptional(provider(), infoTag.getCompound("preserved"));
        if (fluidStack.isEmpty()) {
            return "";
        }
        String name = I18n.get(fluidStack.getTranslationKey());
        return name + " (" + fluidStack.getAmount() + "mb)";
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack ret = new ItemStack(this);
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof TankTileEntity tank) {
            BlockItem.setBlockEntityData(ret, TankModule.TYPE_TANK.get(), (CompoundTag) tank.saveWithoutMetadata(provider()));
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

    private static HolderLookup.Provider provider() {
        return BUILTIN_PROVIDER;
    }

}
