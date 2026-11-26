package mcjty.deepresonance.modules.core;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.network.chat.Component;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import static mcjty.deepresonance.DeepResonance.MODID;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.block.BlockResonatingPlate;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalBlock;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.core.client.ResonatingCrystalRenderer;
import mcjty.deepresonance.modules.core.data.Crystal;
import mcjty.deepresonance.modules.core.data.LCD;
import mcjty.deepresonance.modules.core.data.ResonatingCrystalData;
import mcjty.deepresonance.modules.core.fluid.FluidLiquidCrystal;
import mcjty.deepresonance.modules.core.item.ItemLiquidInjector;
import mcjty.deepresonance.modules.core.util.CrystalConfig;
import mcjty.deepresonance.modules.core.util.ResonatingPlateBlockConfig;
import mcjty.deepresonance.setup.Registration;
import mcjty.deepresonance.util.DeepResonanceTags;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import mcjty.lib.setup.DeferredHolder;
import mcjty.lib.setup.DeferredItem;

import java.util.List;
import java.util.function.Supplier;

import static mcjty.deepresonance.DeepResonance.tab;
import static mcjty.deepresonance.setup.Registration.TILES;
import static mcjty.lib.datagen.DataGen.has;

public class CoreModule implements IModule {

    public static final String TILE_DATA_TAG = "BlockEntityTag";

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LCD>> ITEM_LCD_DATA = Registration.COMPONENTS.registerComponentType(
            "lcd",
            builder -> builder
                    .persistent(LCD.CODEC)
                    .networkSynchronized(LCD.STREAM_CODEC));
    public static final AttachmentType<Crystal> CRYSTAL_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(MODID, "crystal"),
            builder -> builder.initializer(() -> Crystal.DEFAULT).persistent(Crystal.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Crystal>> ITEM_CRYSTAL_DATA = Registration.COMPONENTS.registerComponentType(
            "crystal",
            builder -> builder
                    .persistent(Crystal.CODEC)
                    .networkSynchronized(Crystal.STREAM_CODEC));

    public static final AttachmentType<ResonatingCrystalData> RESONATING_CRYSTAL_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(MODID, "resonating_crystal"),
            builder -> builder.initializer(() -> ResonatingCrystalData.DEFAULT).persistent(ResonatingCrystalData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResonatingCrystalData>> ITEM_RESONATING_CRYSTAL_DATA = Registration.COMPONENTS.registerComponentType(
            "resonating_crystal",
            builder -> builder
                    .persistent(ResonatingCrystalData.CODEC)
                    .networkSynchronized(ResonatingCrystalData.STREAM_CODEC));

    private static Block ore(Block.Properties properties) {
        return new Block(properties.sound(SoundType.STONE).requiresCorrectToolForDrops().strength(3, 5));
    }

    public static final Supplier<Fluid> LIQUID_CRYSTAL = Registration.FLUIDS.register("liquid_crystal", FluidLiquidCrystal::new);

    public static final RBlock<ResonatingCrystalBlock, BlockItem, BlockEntity> RESONATING_CRYSTAL_NATURAL = Registration.registerSimpleBlock(
            "resonating_crystal_natural",
            properties -> new ResonatingCrystalBlock(properties, false, false)
    );
    public static final RBlock<ResonatingCrystalBlock, BlockItem, BlockEntity> RESONATING_CRYSTAL_NATURAL_EMPTY = Registration.registerSimpleBlock(
            "resonating_crystal_natural_empty",
            properties -> new ResonatingCrystalBlock(properties, false, true)
    );
    public static final RBlock<ResonatingCrystalBlock, BlockItem, BlockEntity> RESONATING_CRYSTAL_GENERATED = Registration.registerSimpleBlock(
            "resonating_crystal_generated",
            properties -> new ResonatingCrystalBlock(properties, true, false)
    );
    public static final RBlock<ResonatingCrystalBlock, BlockItem, BlockEntity> RESONATING_CRYSTAL_GENERATED_EMPTY = Registration.registerSimpleBlock(
            "resonating_crystal_generated_empty",
            properties -> new ResonatingCrystalBlock(properties, true, true)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResonatingCrystalTileEntity>> TYPE_RESONATING_CRYSTAL = TILES.register("resonating_crystal", () -> FabricBlockEntityTypeBuilder.create(ResonatingCrystalTileEntity::new,
                    RESONATING_CRYSTAL_GENERATED.block().get(), RESONATING_CRYSTAL_GENERATED_EMPTY.block().get(),
                    RESONATING_CRYSTAL_NATURAL.block().get(), RESONATING_CRYSTAL_NATURAL_EMPTY.block().get()).build());

    public static final RBlock<Block, BlockItem, BlockEntity> RESONATING_ORE_STONE = Registration.registerSimpleBlock(
            "resonating_ore_stone",
            CoreModule::ore
    );
    public static final RBlock<Block, BlockItem, BlockEntity> RESONATING_ORE_DEEPSLATE = Registration.registerSimpleBlock(
            "resonating_ore_deepslate",
            CoreModule::ore
    );
    public static final RBlock<Block, BlockItem, BlockEntity> RESONATING_ORE_NETHER = Registration.registerSimpleBlock(
            "resonating_ore_nether",
            CoreModule::ore
    );
    public static final RBlock<Block, BlockItem, BlockEntity> RESONATING_ORE_END = Registration.registerSimpleBlock(
            "resonating_ore_end",
            CoreModule::ore
    );
    public static final RBlock<BlockResonatingPlate, BlockItem, BlockEntity> RESONATING_PLATE_BLOCK = Registration.registerSimpleBlock(
            "resonating_plate_block",
            properties -> new BlockResonatingPlate(properties
                    .sound(SoundType.STONE)
                    .strength(3, 5)),
            (block, properties) -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix())
    );

    public static final DeferredItem<Item> RESONATING_PLATE_ITEM = Registration.ITEMS.register("resonating_plate", tab(Item::new));
    public static final DeferredItem<Item> FILTER_MATERIAL_ITEM = Registration.ITEMS.register("filter_material", tab(Item::new));
    public static final DeferredItem<Item> SPENT_FILTER_ITEM = Registration.ITEMS.register("spent_filter_material", tab(Item::new));
    public static final DeferredItem<Item> LIQUID_INJECTOR_ITEM = Registration.ITEMS.register("liquid_injector", tab(ItemLiquidInjector::new));
    public static final DeferredItem<Item> MACHINE_FRAME_ITEM = Registration.ITEMS.register("machine_frame", tab(Item::new));

    @Override
    public void init() {
        FluidVariantAttributes.register(LIQUID_CRYSTAL.get(), new FluidVariantAttributeHandler() {
            @Override
            public Component getName(FluidVariant variant) {
                return Component.translatable("fluid.deepresonance.liquid_crystal");
            }
        });
    }

    @Override
    public void initClient() {
        ResonatingCrystalRenderer.register();
    }

    @Override
    public void initConfig() {
        CrystalConfig.init();
        ResonatingPlateBlockConfig.init();
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider registries) {
        dataGen.add(
                Dob.builder(RESONATING_ORE_DEEPSLATE.block(), RESONATING_ORE_DEEPSLATE.item())
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(ConventionalBlockTags.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(ConventionalItemTags.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
                Dob.builder(RESONATING_ORE_END.block(), RESONATING_ORE_END.item())
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(ConventionalBlockTags.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(ConventionalItemTags.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
                Dob.builder(RESONATING_ORE_NETHER.block(), RESONATING_ORE_NETHER.item())
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(ConventionalBlockTags.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(ConventionalItemTags.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
                Dob.builder(RESONATING_ORE_STONE.block(), RESONATING_ORE_STONE.item())
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(ConventionalBlockTags.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(ConventionalItemTags.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
                Dob.blockBuilder(RESONATING_PLATE_BLOCK)
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .shaped(builder -> builder
                                        .unlockedBy("has_resonant_plate", has(RESONATING_PLATE_ITEM.get()))
                                        .define('P', RESONATING_PLATE_ITEM.get()),
                                "PPP", "PPP", "PPP"),
                Dob.blockBuilder(RESONATING_CRYSTAL_NATURAL_EMPTY)
                        .standardLoot(ITEM_CRYSTAL_DATA.get())
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_NATURAL_EMPTY.block(), provider, "crystal_empty", "crystal", "empty_crystal");
                        })
                        .parentedItem("block/crystal_empty")
                        .diamondPickaxeTags(),
                Dob.blockBuilder(RESONATING_CRYSTAL_NATURAL)
                        .standardLoot(ITEM_CRYSTAL_DATA.get())
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_NATURAL.block(), provider, "crystal_full", "crystal", "crystal");
                        })
                        .parentedItem("block/crystal_full")
                        .diamondPickaxeTags(),
                Dob.blockBuilder(RESONATING_CRYSTAL_GENERATED_EMPTY)
                        .standardLoot(ITEM_CRYSTAL_DATA.get())
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_GENERATED_EMPTY.block(), provider, "crystal_empty_pure", "crystal_generated", "empty_crystal");
                        })
                        .parentedItem("block/crystal_empty_pure")
                        .diamondPickaxeTags(),
                Dob.blockBuilder(RESONATING_CRYSTAL_GENERATED)
                        .standardLoot(ITEM_CRYSTAL_DATA.get())
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_GENERATED.block(), provider, "crystal_full_pure", "crystal_generated", "crystal");
                        })
                        .parentedItem("block/crystal_full_pure")
                        .diamondPickaxeTags(),
                Dob.itemBuilder(FILTER_MATERIAL_ITEM)
                        .generatedItem("item/filter_material")
                        .shaped(builder -> builder
                                        .define('g', ConventionalItemTags.GRAVELS)
                                        .define('s', ItemTags.SAND)
                                        .unlockedBy("has_gravel", has(ConventionalItemTags.GRAVELS)),
                                8,
                                "gcg", "csc", "gcg"),
                Dob.itemBuilder(LIQUID_INJECTOR_ITEM)
                        .generatedItem("item/liquid_injector"),
                Dob.itemBuilder(RESONATING_PLATE_ITEM)
                        .generatedItem("item/resonating_plate"),
                Dob.itemBuilder(SPENT_FILTER_ITEM)
                        .generatedItem("item/spent_filter_material"),
                Dob.itemBuilder(MACHINE_FRAME_ITEM)
                        .cubeAll(Identifier.fromNamespaceAndPath(DeepResonance.MODID, "block/machine_side"))
                        .shaped(builder -> builder
                                        .define('g', ConventionalItemTags.STONES)
                                        .define('P', RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS)),
                                "iPi", "PgP", "iPi")
        );

    }
}
