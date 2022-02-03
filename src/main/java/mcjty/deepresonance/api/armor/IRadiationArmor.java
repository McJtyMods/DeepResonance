package mcjty.deepresonance.api.armor;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public interface IRadiationArmor {

    default float getEffectiveness(ItemStack armorStack, EquipmentSlotType slot, float radiationStrength) {
        return 1;
    }

    default boolean isActive(ItemStack armorItemStack) {
        return true;
    }
}
