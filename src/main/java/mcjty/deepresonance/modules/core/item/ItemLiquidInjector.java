package mcjty.deepresonance.modules.core.item;

import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.modules.tank.data.DRTankHandler;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.Tools;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

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
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flags) {
        super.appendHoverText(stack, level, tooltip, flags);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), stack, tooltip, flags);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) {
            BlockEntity tile = world.getBlockEntity(context.getClickedPos());
            if (tile instanceof TankTileEntity tank) {
                DRTankHandler fluidHandler = tank.getInternalFluidHandler();
                fluidHandler.fill(LiquidCrystalData.makeLiquidCrystalStack(100, 1.0f, 0.1f, 0.1f, 0.1f), IFluidHandler.FluidAction.EXECUTE);
            } else if (context.getPlayer() != null) {
                context.getPlayer().sendSystemMessage(ComponentFactory.translatable("message.deepresonance.no_tank").withStyle(ChatFormatting.YELLOW));
            }
        }
        return InteractionResult.SUCCESS;
    }

}
