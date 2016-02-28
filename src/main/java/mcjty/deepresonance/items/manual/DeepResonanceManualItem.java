package mcjty.deepresonance.items.manual;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.deepresonance.items.GenericDRItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            player.openGui(DeepResonance.instance, GuiProxy.GUI_MANUAL_MAIN, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
            return stack;
        }
        return stack;
    }

}
