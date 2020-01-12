package mcjty.deepresonance.modules.core;

import elec332.core.api.module.ElecModule;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.item.ItemLiquidInjector;
import mcjty.deepresonance.util.ItemWithTooltip;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

/**
 * Created by Elec332 on 10-1-2020
 */
@ElecModule(owner = DeepResonance.MODID, name = "Core", alwaysEnabled = true)
public class CoreModule {

    public static final RegistryObject<Item> RESONATING_PLATE_ITEM = DeepResonance.ITEMS.register("resonating_plate", () -> new Item(DeepResonance.createStandardProperties()));
    public static final RegistryObject<Item> FILTER_MATERIAL_ITEM = DeepResonance.ITEMS.register("filter_material", () -> new ItemWithTooltip(DeepResonance.createStandardProperties()));
    public static final RegistryObject<Item> SPENT_FILTER_ITEM = DeepResonance.ITEMS.register("spent_filter", () -> new Item(DeepResonance.createStandardProperties()));
    public static final RegistryObject<Item> LIQUID_INJECTOR_ITEM = DeepResonance.ITEMS.register("liquid_injector", () -> new ItemLiquidInjector(DeepResonance.createStandardProperties()));

}
