package mcjty.deepresonance.items.manual;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.deepresonance.items.GenericDRItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class DeepResonanceManualItem extends GenericDRItem {

    public DeepResonanceManualItem() {
        super("dr_manual");
        setMaxStackSize(1);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            player.openGui(DeepResonance.instance, GuiProxy.GUI_MANUAL_MAIN, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

}
