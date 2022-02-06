package mcjty.deepresonance.modules.radiation.item;

import mcjty.deepresonance.api.armor.IRadiationArmor;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.deepresonance.setup.Registration;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.sounds.SoundEvents;

public class ItemRadiationSuit extends ArmorItem implements IRadiationArmor {

    public static final ArmorMaterial ARMOR_TYPE = new ResonatingMaterial("deepresonance:resonating_armor",
            12, new int[]{1, 4, 5, 2}, 12, SoundEvents.ARMOR_EQUIP_DIAMOND, 0.5f, 0.0f /* @todo 1.16 what is this?*/,
            () -> Ingredient.of(CoreModule.RESONATING_PLATE_ITEM.get()));

    public ItemRadiationSuit(EquipmentSlot slot) {
        super(ARMOR_TYPE, slot, Registration.createStandardProperties());
    }

    public static float getRadiationProtection(LivingEntity entity){
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (!stack.isEmpty()) {
                    int count = countSuitPieces(entity);
                    if (count <= 0) {
                        return 0.0f;
                    } else if (stack.getItem() instanceof IRadiationArmor && ((IRadiationArmor) stack.getItem()).isActive(stack)) {
                        return ((IRadiationArmor) stack.getItem()).protection(count);
                    } else if (stack.hasTag() && stack.getTag().contains("AntiRadiationArmor")) {
                        return (float) (double) RadiationConfiguration.SUIT_PROTECTION[count].get();
                    }
                }
            }
        }
        return 0;
    }

    public static int countSuitPieces(LivingEntity entity){
        int cnt = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (!stack.isEmpty() && (stack.getItem() instanceof IRadiationArmor) && ((IRadiationArmor)stack.getItem()).isActive(stack)) {
                    cnt++;
                } else if (stack.hasTag() && stack.getTag().contains("AntiRadiationArmor")) {
                    cnt++;
                }
            }
        }

        return cnt;
    }

}
