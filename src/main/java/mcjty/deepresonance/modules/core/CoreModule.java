package mcjty.deepresonance.modules.core;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.block.BlockResonatingPlate;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalBlock;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.core.client.ResonatingCrystalRenderer;
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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static mcjty.deepresonance.DeepResonance.tab;
import static mcjty.deepresonance.setup.Registration.TILES;
import static mcjty.lib.datagen.DataGen.has;

public class CoreModule implements IModule {

    public static final String TILE_DATA_TAG = "BlockEntityTag";

    private static final Block.Properties ORE_PROPERTIES = Block.Properties.of()
            .sound(SoundType.STONE)
            .requiresCorrectToolForDrops()
            .strength(3, 5);

    public static final Supplier<FluidType> LIQUID_CRYSTAL_TYPE = Registration.FLUID_TYPES.register("liquid_crystal_type",
            () -> new FluidType(FluidType.Properties.create()) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new FluidLiquidCrystal.ClientExtensions());
                }

                @Override
                public String getDescriptionId() {
                    return "fluid.deepresonance.liquid_crystal";
                }
            });
    public static final Supplier<Fluid> LIQUID_CRYSTAL = Registration.FLUIDS.register("liquid_crystal", FluidLiquidCrystal::new);

    public static final RBlock<ResonatingCrystalBlock, BlockItem, BlockEntity> RESONATING_CRYSTAL_NATURAL = Registration.registerSimpleBlock(
            "resonating_crystal_natural",
            () -> new ResonatingCrystalBlock(false, false)
    );
    public static final RBlock<ResonatingCrystalBlock, BlockItem, BlockEntity> RESONATING_CRYSTAL_NATURAL_EMPTY = Registration.registerSimpleBlock(
            "resonating_crystal_natural_empty",
            () -> new ResonatingCrystalBlock(false, true)
    );
    public static final RBlock<ResonatingCrystalBlock, BlockItem, BlockEntity> RESONATING_CRYSTAL_GENERATED = Registration.registerSimpleBlock(
            "resonating_crystal_generated",
            () -> new ResonatingCrystalBlock(true, false)
    );
    public static final RBlock<ResonatingCrystalBlock, BlockItem, BlockEntity> RESONATING_CRYSTAL_GENERATED_EMPTY = Registration.registerSimpleBlock(
            "resonating_crystal_generated_empty",
            () -> new ResonatingCrystalBlock(true, true)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResonatingCrystalTileEntity>> TYPE_RESONATING_CRYSTAL = TILES.register("resonating_crystal", () -> BlockEntityType.Builder.of(ResonatingCrystalTileEntity::new,
                    RESONATING_CRYSTAL_GENERATED.block().get(), RESONATING_CRYSTAL_GENERATED_EMPTY.block().get(),
                    RESONATING_CRYSTAL_NATURAL.block().get(), RESONATING_CRYSTAL_NATURAL_EMPTY.block().get())
            .build(null));

    public static final RBlock<Block, BlockItem, BlockEntity> RESONATING_ORE_STONE = Registration.registerSimpleBlock(
            "resonating_ore_stone",
            () -> new Block(ORE_PROPERTIES)
    );
    public static final RBlock<Block, BlockItem, BlockEntity> RESONATING_ORE_DEEPSLATE = Registration.registerSimpleBlock(
            "resonating_ore_deepslate",
            () -> new Block(ORE_PROPERTIES)
    );
    public static final RBlock<Block, BlockItem, BlockEntity> RESONATING_ORE_NETHER = Registration.registerSimpleBlock(
            "resonating_ore_nether",
            () -> new Block(ORE_PROPERTIES)
    );
    public static final RBlock<Block, BlockItem, BlockEntity> RESONATING_ORE_END = Registration.registerSimpleBlock(
            "resonating_ore_end",
            () -> new Block(ORE_PROPERTIES)
    );
    public static final RBlock<BlockResonatingPlate, BlockItem, BlockEntity> RESONATING_PLATE_BLOCK = Registration.registerSimpleBlock(
            "resonating_plate_block",
            () -> new BlockResonatingPlate(Block.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(3, 5)),
            block -> new BlockItem(block.get(), Registration.createStandardProperties())
    );

    public static final DeferredItem<Item> RESONATING_PLATE_ITEM = Registration.ITEMS.register("resonating_plate", tab(() -> new Item(Registration.createStandardProperties())));
    public static final DeferredItem<Item> FILTER_MATERIAL_ITEM = Registration.ITEMS.register("filter_material", tab(() -> new Item(Registration.createStandardProperties())));
    public static final DeferredItem<Item> SPENT_FILTER_ITEM = Registration.ITEMS.register("spent_filter_material", tab(() -> new Item(Registration.createStandardProperties())));
    public static final DeferredItem<Item> LIQUID_INJECTOR_ITEM = Registration.ITEMS.register("liquid_injector", tab(() -> new ItemLiquidInjector(Registration.createStandardProperties())));
    public static final DeferredItem<Item> MACHINE_FRAME_ITEM = Registration.ITEMS.register("machine_frame", tab(() -> new Item(Registration.createStandardProperties())));

    public CoreModule() {
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        ResonatingCrystalRenderer.register();
    }

    @Override
    public void initConfig(IEventBus bus) {
        CrystalConfig.init();
        ResonatingPlateBlockConfig.init(bus);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider registries) {
        dataGen.add(
                Dob.builder(RESONATING_ORE_DEEPSLATE.block(), RESONATING_ORE_DEEPSLATE.item())
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(Tags.Blocks.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(Tags.Items.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
                Dob.builder(RESONATING_ORE_END.block(), RESONATING_ORE_END.item())
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(Tags.Blocks.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(Tags.Items.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
                Dob.builder(RESONATING_ORE_NETHER.block(), RESONATING_ORE_NETHER.item())
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(Tags.Blocks.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(Tags.Items.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
                Dob.builder(RESONATING_ORE_STONE.block(), RESONATING_ORE_STONE.item())
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(Tags.Blocks.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(Tags.Items.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
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
//                        .standardLoot(TYPE_RESONATING_CRYSTAL)    // @todo 1.21
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_NATURAL_EMPTY.block(), provider, "crystal_empty", "crystal", "empty_crystal");
                        })
                        .parentedItem("block/crystal_empty")
                        .diamondPickaxeTags(),
                Dob.blockBuilder(RESONATING_CRYSTAL_NATURAL)
//                        .standardLoot(TYPE_RESONATING_CRYSTAL)    // @todo 1.21
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_NATURAL.block(), provider, "crystal_full", "crystal", "crystal");
                        })
                        .parentedItem("block/crystal_full")
                        .diamondPickaxeTags(),
                Dob.blockBuilder(RESONATING_CRYSTAL_GENERATED_EMPTY)
//                        .standardLoot(TYPE_RESONATING_CRYSTAL)    // @todo 1.21
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_GENERATED_EMPTY.block(), provider, "crystal_empty_pure", "crystal_generated", "empty_crystal");
                        })
                        .parentedItem("block/crystal_empty_pure")
                        .diamondPickaxeTags(),
                Dob.blockBuilder(RESONATING_CRYSTAL_GENERATED)
//                        .standardLoot(TYPE_RESONATING_CRYSTAL)    // @todo 1.21
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_GENERATED.block(), provider, "crystal_full_pure", "crystal_generated", "crystal");
                        })
                        .parentedItem("block/crystal_full_pure")
                        .diamondPickaxeTags(),
                Dob.itemBuilder(FILTER_MATERIAL_ITEM)
                        .generatedItem("item/filter_material")
                        .shaped(builder -> builder
                                        .define('g', Tags.Items.GRAVELS)
                                        .define('s', ItemTags.SAND)
                                        .unlockedBy("has_gravel", has(Tags.Items.GRAVELS)),
                                8,
                                "gcg", "csc", "gcg"),
                Dob.itemBuilder(LIQUID_INJECTOR_ITEM)
                        .generatedItem("item/liquid_injector"),
                Dob.itemBuilder(RESONATING_PLATE_ITEM)
                        .generatedItem("item/resonating_plate"),
                Dob.itemBuilder(SPENT_FILTER_ITEM)
                        .generatedItem("item/spent_filter_material"),
                Dob.itemBuilder(MACHINE_FRAME_ITEM)
                        .cubeAll(ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "block/machine_side"))
                        .shaped(builder -> builder
                                        .define('g', Tags.Items.STONES)
                                        .define('P', RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)),
                                "iPi", "PgP", "iPi")
        );

    }
}
