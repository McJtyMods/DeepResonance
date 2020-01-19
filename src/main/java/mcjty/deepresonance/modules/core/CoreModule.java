package mcjty.deepresonance.modules.core;

import com.google.common.base.Preconditions;
import elec332.core.api.config.IConfigWrapper;
import elec332.core.api.module.ElecModule;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.block.BlockCrystal;
import mcjty.deepresonance.modules.core.block.BlockMachineFrame;
import mcjty.deepresonance.modules.core.item.ItemLiquidInjector;
import mcjty.deepresonance.modules.core.util.CrystalConfig;
import mcjty.deepresonance.util.ItemWithTooltip;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Created by Elec332 on 10-1-2020
 */
@ElecModule(owner = DeepResonance.MODID, name = "Core", alwaysEnabled = true)
public class CoreModule {

    public static final RegistryObject<Block> MACHINE_FRAME_BLOCK = DeepResonance.BLOCKS.register("machine_frame", BlockMachineFrame::new);
    public static final RegistryObject<Block> RESONATING_CRYSTAL_BLOCK = DeepResonance.BLOCKS.register("resonating_crystal", BlockCrystal::new);

    public static final RegistryObject<Item> RESONATING_PLATE_ITEM = DeepResonance.ITEMS.register("resonating_plate", () -> new Item(DeepResonance.createStandardProperties()));
    public static final RegistryObject<Item> FILTER_MATERIAL_ITEM = DeepResonance.ITEMS.register("filter_material", () -> new ItemWithTooltip(DeepResonance.createStandardProperties()));
    public static final RegistryObject<Item> SPENT_FILTER_ITEM = DeepResonance.ITEMS.register("spent_filter", () -> new Item(DeepResonance.createStandardProperties()));
    public static final RegistryObject<Item> LIQUID_INJECTOR_ITEM = DeepResonance.ITEMS.register("liquid_injector", () -> new ItemLiquidInjector(DeepResonance.createStandardProperties()));
    public static final RegistryObject<Item> MACHINE_FRAME_ITEM = DeepResonance.ITEMS.register("machine_frame", () -> new BlockItem(Preconditions.checkNotNull(MACHINE_FRAME_BLOCK.get()), DeepResonance.createStandardProperties()));
    public static final RegistryObject<Item> RESONATING_CRYSTAL_ITEM = DeepResonance.ITEMS.register("resonating_crystal", () -> new BlockItem(Preconditions.checkNotNull(RESONATING_CRYSTAL_BLOCK.get()), DeepResonance.createStandardProperties()));

    public static IConfigWrapper config;

    public CoreModule() {
        config = DeepResonance.config.getSubConfig("core_module", "Core module settings");
        config.registerConfigurableElement(new CrystalConfig(), "resonating_crystal", "Resonating Crystal settings");
    }

}
