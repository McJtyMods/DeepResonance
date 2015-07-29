package mcjty.deepresonance.items;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.items.manual.DeepResonanceManualItem;

public final class ModItems {
    public static DeepResonanceManualItem deepResonanceManualItem;

    public static void init() {
        deepResonanceManualItem = new DeepResonanceManualItem();
        deepResonanceManualItem.setUnlocalizedName("DeepResonanceManual");
        deepResonanceManualItem.setCreativeTab(DeepResonance.tabDeepResonance);
        deepResonanceManualItem.setTextureName(DeepResonance.MODID + ":deepResonanceManual");
        GameRegistry.registerItem(deepResonanceManualItem, "deepResonanceManualItem");

    }
}
