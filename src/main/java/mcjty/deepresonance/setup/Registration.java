package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.radiation.item.ItemRadiationSuit;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.blocks.RBlockRegistry;
import mcjty.lib.setup.DeferredBlocks;
import mcjty.lib.setup.DeferredItems;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

import static mcjty.deepresonance.DeepResonance.tab;

public class Registration {

    public static final RBlockRegistry RBLOCKS = new RBlockRegistry(DeepResonance.MODID, supplier -> DeepResonance.setup.addTabItem(supplier));
    public static final DeferredItems ITEMS = DeferredItems.create(DeepResonance.MODID);
    public static final DeferredBlocks BLOCKS = DeferredBlocks.create(DeepResonance.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, DeepResonance.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, DeepResonance.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, DeepResonance.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, DeepResonance.MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, DeepResonance.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DeepResonance.MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, DeepResonance.MODID);
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, DeepResonance.MODID);

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, DeepResonance.MODID);
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, DeepResonance.MODID);

    public static void register(IEventBus bus) {
        RBLOCKS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        CONTAINERS.register(bus);
        TILES.register(bus);
        SOUNDS.register(bus);
        FEATURES.register(bus);
        ARMOR_MATERIALS.register(bus);
        TABS.register(bus);
        ATTACHMENT_TYPES.register(bus);
        COMPONENTS.register(bus);
        ItemRadiationSuit.init();
    }

    public static Item.Properties createStandardProperties() {
        return DeepResonance.setup.defaultProperties();
    }

    public static <B extends Block> DeferredItem<BlockItem> fromBlock(DeferredBlock<B> block) {
        return ITEMS.register(block.getId().getPath(), tab(() -> new BlockItem(block.get(), createStandardProperties())));
    }

    public static <B extends Block> RBlock<B, BlockItem, BlockEntity> registerSimpleBlock(String name, Supplier<B> blockSupplier) {
        return registerSimpleBlock(name, blockSupplier, block -> new BlockItem(block.get(), createStandardProperties()));
    }

    public static <B extends Block, I extends BlockItem> RBlock<B, I, BlockEntity> registerSimpleBlock(String name,
                                                                                                      Supplier<B> blockSupplier,
                                                                                                      Function<Supplier<? extends Block>, I> itemFactory) {
        DeferredBlock<B> block = BLOCKS.register(name, blockSupplier);
        DeferredItem<I> item = ITEMS.register(name, tab(() -> itemFactory.apply(block)));
        return new RBlock<>(block, item, null);
    }

    public static <B extends BaseBlock, I extends BlockItem, E extends GenericTileEntity> RBlock<B, I, E> registerBlockWithTile(
            String name,
            Class<E> clazz,
            Supplier<B> blockSupplier,
            Function<Supplier<? extends Block>, I> itemFactory,
            BlockEntityType.BlockEntitySupplier<E> tileSupplier) {
        return RBLOCKS.registerBlock(name, clazz, blockSupplier, itemFactory, tileSupplier);
    }

    public static Supplier<CreativeModeTab> TAB = TABS.register("deepresonance", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + DeepResonance.MODID))
            .icon(() -> new ItemStack(CoreModule.RESONATING_CRYSTAL_GENERATED.block().get()))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .displayItems((featureFlags, output) -> {
                DeepResonance.setup.populateTab(output);
            })
            .build());
}
