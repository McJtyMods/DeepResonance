package mcjty.deepresonance.modules.core.item;

import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class ItemLiquidInjector extends Item implements ITooltipSettings {

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(key("message.deepresonance.shiftmessage"))
            .infoShift(header());


    public ItemLiquidInjector(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World level, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flags) {
        super.appendHoverText(stack, level, tooltip, flags);
        tooltipBuilder.get().makeTooltip(getRegistryName(), stack, tooltip, flags);
    }

    @Nonnull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (!world.isClientSide) {
            TileEntity tile = world.getBlockEntity(context.getClickedPos());
            if (tile instanceof TankTileEntity) {
                LazyOptional<IFluidHandler> fluidHanderCap = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
                fluidHanderCap.ifPresent(fluidHander -> fluidHander.fill(DeepResonanceFluidHelper.makeLiquidCrystalStack(100, 1.0f, 0.1f, 0.1f, 0.1f), IFluidHandler.FluidAction.EXECUTE));
            } else if (context.getPlayer() != null) {
                context.getPlayer().sendMessage(new TranslationTextComponent("message.deepresonance.no_tank").withStyle(TextFormatting.YELLOW), Util.NIL_UUID);
            }
        }
        return ActionResultType.SUCCESS;
    }

}
