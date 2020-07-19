package mcjty.deepresonance.modules.radiation.util;

import com.google.common.base.Preconditions;
import mcjty.deepresonance.api.armor.IRadiationArmor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/**
 * Created by Elec332 on 13-7-2020
 */
public class RadiationHelper {

    private static final float[] PROTECTION_COUNT_VALUES = {0.75f, 0.81f, 0.89f, 1.0f};

    private static final int[] PIECE_PROTECTION_VALUES = {12, 24, 35, 29};

    public static float getSuitProtection(LivingEntity entity, float strength) {
        int cnt = 0;
        float ret = 0;
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                ItemStack stack = entity.getItemStackFromSlot(slot);
                float f = 0;
                if (!stack.isEmpty() && stack.getItem() instanceof IRadiationArmor) {
                    IRadiationArmor armor = (IRadiationArmor) stack.getItem();
                    f = armor.getEffectiveness(stack, slot, strength);
                    cnt++;
                } else if (stack.hasTag() && Preconditions.checkNotNull(stack.getTag()).contains("AntiRadiationArmor")) {
                    f = 1;
                    cnt++;
                }
                ret += PIECE_PROTECTION_VALUES[slot.getIndex()] * MathHelper.clamp(f, -1, 1);
            }
        }
        return (ret / 100) * PROTECTION_COUNT_VALUES[cnt];
    }

    public static float calculateRadiationStrength(float strength, float purity) {
        float p = (float) Math.log10(purity / 100.0f) + 1.0f;
        if (p < 0.01f) {
            p = 0.01f;
        }
        return (float) (RadiationConfiguration.MIN_RADIATION_STRENGTH.get() + strength * (1.0f - p) / 100.0f
                * (RadiationConfiguration.MAX_RADIATION_STRENGTH.get() - RadiationConfiguration.MIN_RADIATION_STRENGTH.get()));
    }

    public static float calculateRadiationRadius(float strength, float efficiency, float purity) {
        float radius = (float) (RadiationConfiguration.MIN_RADIATION_RADIUS.get() + (strength + efficiency) / 200.0f
                * (RadiationConfiguration.MAX_RADIATION_RADIUS.get() - RadiationConfiguration.MIN_RADIATION_RADIUS.get()));
        radius += radius * (100.0f - purity) * .002f;
        return radius;
    }

}
