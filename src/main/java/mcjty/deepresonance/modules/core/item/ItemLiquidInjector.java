package mcjty.deepresonance.modules.core.item;

import mcjty.deepresonance.modules.tank.tile.TileEntityTank;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.deepresonance.util.TranslationHelper;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 10-1-2020
 */
public class ItemLiquidInjector extends Item {

    public ItemLiquidInjector(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flags) {
        super.appendHoverText(stack, level, tooltip, flags);
        tooltip.add(new TranslationTextComponent(TranslationHelper.getTooltipKey(this)));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (!world.isClientSide) {
            TileEntity tile = world.getBlockEntity(context.getClickedPos());
            if (tile instanceof TileEntityTank) {
                LazyOptional<IFluidHandler> fluidHanderCap = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
                fluidHanderCap.ifPresent(fluidHander -> fluidHander.fill(DeepResonanceFluidHelper.makeLiquidCrystalStack(100, 1.0f, 0.1f, 0.1f, 0.1f), IFluidHandler.FluidAction.EXECUTE));
            } else if (context.getPlayer() != null) {
                context.getPlayer().sendMessage(new TranslationTextComponent(TranslationHelper.getMessageKey("no_tank")).withStyle(TextFormatting.YELLOW), Util.NIL_UUID);
            }
        }
        return ActionResultType.SUCCESS;
    }

}
