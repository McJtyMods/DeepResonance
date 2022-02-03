package mcjty.deepresonance.modules.radiation.item;

import mcjty.deepresonance.api.armor.IRadiationArmor;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.deepresonance.setup.Registration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvents;

public class ItemRadiationSuit extends ArmorItem implements IRadiationArmor {

    public static final IArmorMaterial ARMOR_TYPE = new ResonatingMaterial("deepresonance:resonating_armor",
            12, new int[]{1, 4, 5, 2}, 12, SoundEvents.ARMOR_EQUIP_DIAMOND, 0.5f, 0.0f /* @todo 1.16 what is this?*/,
            () -> Ingredient.of(CoreModule.RESONATING_PLATE_ITEM.get()));

    public ItemRadiationSuit(EquipmentSlotType slot) {
        super(ARMOR_TYPE, slot, Registration.createStandardProperties());
    }

    @Override
    public float getEffectiveness(ItemStack armorStack, EquipmentSlotType slot, float radiationStrength) {
        return 0.9f;
    }

    public static float getRadiationProtection(LivingEntity entity){
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getType() == EquipmentSlotType.Group.ARMOR) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof IRadiationArmor && ((IRadiationArmor) stack.getItem()).isActive(stack)) {
                        return ((IRadiationArmor) stack.getItem()).protection()[countSuitPieces(entity)];
                    } else if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AntiRadiationArmor")) {
                        return RadiationConfiguration.suitProtection[countSuitPieces(entity)];
                    }
                }
            }
        }
        return 0;
    }
}
