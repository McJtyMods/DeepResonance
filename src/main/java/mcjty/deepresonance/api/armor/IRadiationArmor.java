package mcjty.deepresonance.api.armor;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 13-7-2020
 */
public interface IRadiationArmor {

    default float getEffectiveness(ItemStack armorStack, EquipmentSlotType slot, float radiationStrength) {
        return 1;
    }

}
