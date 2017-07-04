package mcjty.deepresonance.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class FilterMaterialItem extends GenericDRItem {

    public FilterMaterialItem() {
        super("filter");
    }

    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);
        list.add("This material can be used in the purifier");
        list.add("to purify the crystal liquid");
    }
}
