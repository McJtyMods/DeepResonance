package mcjty.deepresonance.modules.radiation;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.radiation.client.RadiationOverlayRenderer;
import mcjty.deepresonance.modules.radiation.item.ItemRadiationSuit;
import mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.List;

import static mcjty.deepresonance.DeepResonance.tab;

public class RadiationModule implements IModule {

    public static final DeferredBlock<Block> POISONED_DIRT_BLOCK = Registration.BLOCKS.register("poisoned_dirt", () -> new Block(Block.Properties.of().mapColor(MapColor.DIRT).strength(0.5f).sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> DENSE_GLASS_BLOCK = Registration.BLOCKS.register("dense_glass",
            () -> new GlassBlock(Block.Properties.of()
                    .strength(3.0f, 500.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .sound(SoundType.GLASS)));
    public static final DeferredBlock<Block> DENSE_OBSIDIAN_BLOCK = Registration.BLOCKS.register("dense_obsidian", () -> new Block(Block.Properties.of().mapColor(MapColor.COLOR_BLACK)
            .strength(50.0f, 2000.0f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)));

    public static final DeferredItem<Item> POISONED_DIRT_ITEM = Registration.fromBlock(POISONED_DIRT_BLOCK);
    public static final DeferredItem<Item> DENSE_GLASS_ITEM = Registration.fromBlock(DENSE_GLASS_BLOCK);
    public static final DeferredItem<Item> DENSE_OBSIDIAN_ITEM = Registration.fromBlock(DENSE_OBSIDIAN_BLOCK);
    public static final DeferredItem<RadiationMonitorItem> RADIATION_MONITOR = Registration.ITEMS.register("radiation_monitor", tab(() -> new RadiationMonitorItem(Registration.createStandardProperties().stacksTo(1))));

    public static final DeferredItem<Item> RADIATION_SUIT_HELMET = Registration.ITEMS.register("radiation_suit_helmet", tab(() -> new ItemRadiationSuit(EquipmentSlot.HEAD)));
    public static final DeferredItem<Item> RADIATION_SUIT_CHESTPLATE = Registration.ITEMS.register("radiation_suit_chestplate", tab(() -> new ItemRadiationSuit(EquipmentSlot.CHEST)));
    public static final DeferredItem<Item> RADIATION_SUIT_LEGGINGS = Registration.ITEMS.register("radiation_suit_leggings", tab(() -> new ItemRadiationSuit(EquipmentSlot.LEGS)));
    public static final DeferredItem<Item> RADIATION_SUIT_BOOTS = Registration.ITEMS.register("radiation_suit_boots", tab(() -> new ItemRadiationSuit(EquipmentSlot.FEET)));

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
    public void initConfig(IEventBus bus) {
        RadiationConfiguration.init();
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(POISONED_DIRT_BLOCK)
                        .simpleBlockState()
                        .parentedItem()
                        .simpleLoot()
                        .blockTags(List.of(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.NEEDS_IRON_TOOL)),
                Dob.blockBuilder(DENSE_GLASS_BLOCK)
                        .blockState(provider -> provider.simpleBlockC(RadiationModule.DENSE_GLASS_BLOCK.get(), modelBuilder -> modelBuilder.renderType("cutout")))
                        .parentedItem()
                        .simpleLoot()
                        .ironPickaxeTags()
                        .shaped(builder -> builder
                                        .define('f', CoreModule.SPENT_FILTER_ITEM.get())
                                        .unlockedBy("has_spent_filter", DataGen.has(CoreModule.SPENT_FILTER_ITEM.get())),
                                "fGf", "GOG", "fGf"),
                Dob.blockBuilder(DENSE_OBSIDIAN_BLOCK)
                        .simpleBlockState()
                        .parentedItem()
                        .simpleLoot()
                        .diamondPickaxeTags()
                        .shaped(builder -> builder
                                        .define('f', CoreModule.SPENT_FILTER_ITEM.get())
                                        .unlockedBy("has_spent_filter", DataGen.has(CoreModule.SPENT_FILTER_ITEM.get())),
                                "OfO", "fOf", "OfO"),
                Dob.itemBuilder(RADIATION_SUIT_BOOTS)
                        .generatedItem("item/radiation_suit_boots")
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_resonant_plate", DataGen.has(CoreModule.RESONATING_PLATE_ITEM.get())),
                                "P P", "P P"),
                Dob.itemBuilder(RADIATION_SUIT_CHESTPLATE)
                        .generatedItem("item/radiation_suit_chestplate")
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_resonant_plate", DataGen.has(CoreModule.RESONATING_PLATE_ITEM.get())),
                                "P P", "PPP", "PPP"),
                Dob.itemBuilder(RADIATION_SUIT_HELMET)
                        .generatedItem("item/radiation_suit_helmet")
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_resonant_plate", DataGen.has(CoreModule.RESONATING_PLATE_ITEM.get())),
                                "PPP", "P P"),
                Dob.itemBuilder(RADIATION_SUIT_LEGGINGS)
                        .generatedItem("item/radiation_suit_leggings")
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_resonant_plate", DataGen.has(CoreModule.RESONATING_PLATE_ITEM.get())),
                                "PPP", "P P", "P P"),
                Dob.itemBuilder(RADIATION_MONITOR)
                        .itemModel(DataGenHelper::generateMonitor)
                        .shaped(builder -> builder
                                        .define('C', Items.COMPARATOR)
                                        .define('x', Items.CLOCK)
                                        .define('q', Items.QUARTZ)
                                        .unlockedBy("", DataGen.has(CoreModule.RESONATING_PLATE_ITEM.get())),
                                "qCq", "ror", "qxq"
                        )
        );
    }
}
