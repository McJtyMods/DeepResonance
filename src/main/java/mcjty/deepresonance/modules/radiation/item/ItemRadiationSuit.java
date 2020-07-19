package mcjty.deepresonance.modules.radiation.item;

import elec332.core.item.SimpleArmorMaterial;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.armor.IRadiationArmor;
import mcjty.deepresonance.modules.core.CoreModule;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 14-7-2020
 */
public class ItemRadiationSuit extends ArmorItem implements IRadiationArmor {

    public static IArmorMaterial ARMOR_TYPE = new SimpleArmorMaterial("deepresonance:resonating_armor", 12, new int[]{1, 4, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.5f, () -> Ingredient.fromItems(CoreModule.RESONATING_PLATE_ITEM.get()));

    public ItemRadiationSuit(EquipmentSlotType slot) {
        super(ARMOR_TYPE, slot, DeepResonance.createStandardProperties());
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public float getEffectiveness(ItemStack armorStack, EquipmentSlotType slot, float radiationStrength) {
        return 0.9f;
    }

}
