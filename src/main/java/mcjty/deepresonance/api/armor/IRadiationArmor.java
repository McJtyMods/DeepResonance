package mcjty.deepresonance.api.armor;

import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public interface IRadiationArmor {

    default float getEffectiveness(ItemStack armorStack, EquipmentSlotType slot, float radiationStrength) {
        return 1;
    }

    default float protection(int i) {
        if (i <= 0) {
            return 0.0f;
        } else {
            return (float) (double) RadiationConfiguration.SUIT_PROTECTION[i - 1].get();
        }
    }

    default boolean isActive(ItemStack armorItemStack) {
        return true;
    }
}
