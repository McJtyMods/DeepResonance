package mcjty.deepresonance.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class FilterMaterialItem extends GenericDRItem {

    public FilterMaterialItem() {
        super("filter");
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        list.add("This material can be used in the purifier");
        list.add("to purify the crystal liquid");
    }
}
