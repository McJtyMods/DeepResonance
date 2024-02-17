package mcjty.deepresonance.setup;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredBlocks;
import mcjty.lib.setup.DeferredItem;
import mcjty.lib.setup.DeferredItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.deepresonance.DeepResonance.tab;

public class Registration {

    public static final DeferredItems ITEMS = DeferredItems.create(DeepResonance.MODID);
    public static final DeferredBlocks BLOCKS = DeferredBlocks.create(DeepResonance.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, DeepResonance.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, DeepResonance.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, DeepResonance.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DeepResonance.MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DeepResonance.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DeepResonance.MODID);

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, DeepResonance.MODID);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        CONTAINERS.register(bus);
        TILES.register(bus);
        SOUNDS.register(bus);
        FEATURES.register(bus);
        TABS.register(bus);
    }

    public static Item.Properties createStandardProperties() {
        return DeepResonance.setup.defaultProperties();
    }

    public static <B extends Block> DeferredItem<Item> fromBlock(DeferredBlock<B> block) {
        return ITEMS.register(block.getId().getPath(), tab(() -> new BlockItem(block.get(), createStandardProperties())));
    }

    public static RegistryObject<CreativeModeTab> TAB = TABS.register("deepresonance", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + DeepResonance.MODID))
            .icon(() -> new ItemStack(CoreModule.RESONATING_CRYSTAL_GENERATED.get()))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .displayItems((featureFlags, output) -> {
                DeepResonance.setup.populateTab(output);
            })
            .build());
}
