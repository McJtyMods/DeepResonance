package mcjty.deepresonance.modules.radiation.item;

import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceKey;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.armor.IRadiationArmor;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.lib.items.GenericArmorItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Map;

public class ItemRadiationSuit extends GenericArmorItem implements IRadiationArmor {

    private static final ArmorMaterial ARMOR_MATERIAL = new ArmorMaterial(
            33,
            Map.of(ArmorType.HELMET, 1, ArmorType.CHESTPLATE, 4, ArmorType.LEGGINGS, 5, ArmorType.BOOTS, 2),
            12,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            0.5f,
            0.0f,
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DeepResonance.MODID, "repairs_radiation_suit")),
            ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(DeepResonance.MODID, "resonating_armor"))
    );

    public ItemRadiationSuit(EquipmentSlot slot, Properties properties) {
        super(ARMOR_MATERIAL, slot, properties);
    }

    public static float getRadiationProtection(LivingEntity entity){
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (!stack.isEmpty()) {
                    int count = countSuitPieces(entity);
                    if (count <= 0) {
                        return 0.0f;
                    } else if (stack.getItem() instanceof IRadiationArmor && ((IRadiationArmor) stack.getItem()).isActive(stack)) {
                        return ((IRadiationArmor) stack.getItem()).protection(count);
                    } else if (hasAntiRadiationFlag(stack)) {
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
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (!stack.isEmpty() && (stack.getItem() instanceof IRadiationArmor) && ((IRadiationArmor)stack.getItem()).isActive(stack)) {
                    cnt++;
                } else if (hasAntiRadiationFlag(stack)) {
                    cnt++;
                }
            }
        }

        return cnt;
    }

    private static boolean hasAntiRadiationFlag(ItemStack stack) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        return custom != null && custom.copyTag().contains("AntiRadiationArmor");
    }

}
