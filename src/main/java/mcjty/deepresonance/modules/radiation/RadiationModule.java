package mcjty.deepresonance.modules.radiation;

import mcjty.deepresonance.modules.radiation.client.RadiationOverlayRenderer;
import mcjty.deepresonance.modules.radiation.item.ItemRadiationSuit;
import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.modules.IModule;
import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class RadiationModule implements IModule {

    public static final RegistryObject<Block> POISONED_DIRT_BLOCK = Registration.BLOCKS.register("poisoned_dirt", () -> new Block(Block.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(0.5f).sound(SoundType.GRASS)));
    public static final RegistryObject<Block> DENSE_GLASS_BLOCK = Registration.BLOCKS.register("dense_glass", () -> new GlassBlock(Block.Properties.of(Material.GLASS).strength(3.0f, 500.0f).sound(SoundType.GLASS).noCollission().harvestTool(ToolType.PICKAXE).harvestLevel(2)));
    public static final RegistryObject<Block> DENSE_OBSIDIAN_BLOCK = Registration.BLOCKS.register("dense_obsidian", () -> new Block(Block.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK).strength(50.0f, 2000.0f).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).harvestLevel(3)));

    public static final RegistryObject<Item> POISONED_DIRT_ITEM = Registration.fromBlock(POISONED_DIRT_BLOCK);
    public static final RegistryObject<Item> DENSE_GLASS_ITEM = Registration.fromBlock(DENSE_GLASS_BLOCK);
    public static final RegistryObject<Item> DENSE_OBSIDIAN_ITEM = Registration.fromBlock(DENSE_OBSIDIAN_BLOCK);
    public static final RegistryObject<RadiationMonitorItem> RADIATION_MONITOR = Registration.ITEMS.register("radiation_monitor", () -> new RadiationMonitorItem(Registration.createStandardProperties().stacksTo(1)));

    public static final RegistryObject<Item> RADIATION_SUIT_HELMET = Registration.ITEMS.register("radiation_suit_helmet", () -> new ItemRadiationSuit(EquipmentSlotType.HEAD));
    public static final RegistryObject<Item> RADIATION_SUIT_CHESTPLATE = Registration.ITEMS.register("radiation_suit_chestplate", () -> new ItemRadiationSuit(EquipmentSlotType.CHEST));
    public static final RegistryObject<Item> RADIATION_SUIT_LEGGINGS = Registration.ITEMS.register("radiation_suit_leggings", () -> new ItemRadiationSuit(EquipmentSlotType.LEGS));
    public static final RegistryObject<Item> RADIATION_SUIT_BOOTS = Registration.ITEMS.register("radiation_suit_boots", () -> new ItemRadiationSuit(EquipmentSlotType.FEET));

    public RadiationModule() {
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RadiationMonitorItem.initOverrides(RADIATION_MONITOR.get());
        });
        MinecraftForge.EVENT_BUS.addListener(RadiationOverlayRenderer::onRender);
    }

    @Override
    public void initConfig() {
        RadiationConfiguration.init();
    }
}
