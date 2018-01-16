package mcjty.deepresonance.api;

import mcjty.deepresonance.radiation.RadiationConfiguration;
import net.minecraft.item.ItemStack;

/**
 * @author canitzp
 */
public interface IRadiationArmor {
    
    default float[] protection() {
    
        return RadiationConfiguration.suitProtection;
    }
    
    default boolean isActive(ItemStack armorItemStack) {
        
        return true;
    }
}
