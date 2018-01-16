package mcjty.deepresonance.api;

import mcjty.deepresonance.radiation.RadiationConfiguration;
import net.minecraft.item.ItemStack;

/**
 * @author canitzp
 */
public interface IRadiationArmor {
    
    default float[] protection(ItemStack armorItemStack) {
    
        return RadiationConfiguration.suitProtection;
    }
    
    default boolean isActive(ItemStack armorItemStack) {
        
        return true;
    }
}
