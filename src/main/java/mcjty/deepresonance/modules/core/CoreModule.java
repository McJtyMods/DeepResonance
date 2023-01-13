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
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Consumer;

import static mcjty.deepresonance.DeepResonance.tab;
import static mcjty.deepresonance.setup.Registration.TILES;

public class CoreModule implements IModule {

    public static final String TILE_DATA_TAG = "BlockEntityTag";

    private static final Block.Properties ORE_PROPERTIES = Block.Properties.of(Material.STONE)
            .requiresCorrectToolForDrops()
            .strength(3, 5);

    public static final RegistryObject<FluidType> LIQUID_CRYSTAL_TYPE = Registration.FLUID_TYPES.register("liquid_crystal_type",
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
    public static final RegistryObject<Fluid> LIQUID_CRYSTAL = Registration.FLUIDS.register("liquid_crystal", FluidLiquidCrystal::new);

    public static final RegistryObject<ResonatingCrystalBlock> RESONATING_CRYSTAL_NATURAL = Registration.BLOCKS.register("resonating_crystal_natural", () -> new ResonatingCrystalBlock(false, false));
    public static final RegistryObject<ResonatingCrystalBlock> RESONATING_CRYSTAL_NATURAL_EMPTY = Registration.BLOCKS.register("resonating_crystal_natural_empty", () -> new ResonatingCrystalBlock(false, true));
    public static final RegistryObject<ResonatingCrystalBlock> RESONATING_CRYSTAL_GENERATED = Registration.BLOCKS.register("resonating_crystal_generated", () -> new ResonatingCrystalBlock(true, false));
    public static final RegistryObject<ResonatingCrystalBlock> RESONATING_CRYSTAL_GENERATED_EMPTY = Registration.BLOCKS.register("resonating_crystal_generated_empty", () -> new ResonatingCrystalBlock(true, true));
    public static final RegistryObject<Item> RESONATING_CRYSTAL_NATURAL_ITEM = Registration.fromBlock(RESONATING_CRYSTAL_NATURAL);
    public static final RegistryObject<Item> RESONATING_CRYSTAL_NATURAL_EMPTY_ITEM = Registration.fromBlock(RESONATING_CRYSTAL_NATURAL_EMPTY);
    public static final RegistryObject<Item> RESONATING_CRYSTAL_GENERATED_ITEM = Registration.fromBlock(RESONATING_CRYSTAL_GENERATED);
    public static final RegistryObject<Item> RESONATING_CRYSTAL_GENERATED_EMPTY_ITEM = Registration.fromBlock(RESONATING_CRYSTAL_GENERATED_EMPTY);
    public static final RegistryObject<BlockEntityType<ResonatingCrystalTileEntity>> TYPE_RESONATING_CRYSTAL = TILES.register("resonating_crystal", () -> BlockEntityType.Builder.of(ResonatingCrystalTileEntity::new,
                    RESONATING_CRYSTAL_GENERATED.get(), RESONATING_CRYSTAL_GENERATED_EMPTY.get(),
                    RESONATING_CRYSTAL_NATURAL.get(), RESONATING_CRYSTAL_NATURAL_EMPTY.get())
            .build(null));

    public static final RegistryObject<Block> RESONATING_ORE_STONE_BLOCK = Registration.BLOCKS.register("resonating_ore_stone", () -> new Block(ORE_PROPERTIES));
    public static final RegistryObject<Block> RESONATING_ORE_DEEPSLATE_BLOCK = Registration.BLOCKS.register("resonating_ore_deepslate", () -> new Block(ORE_PROPERTIES));
    public static final RegistryObject<Block> RESONATING_ORE_NETHER_BLOCK = Registration.BLOCKS.register("resonating_ore_nether", () -> new Block(ORE_PROPERTIES));
    public static final RegistryObject<Block> RESONATING_ORE_END_BLOCK = Registration.BLOCKS.register("resonating_ore_end", () -> new Block(ORE_PROPERTIES));
    public static final RegistryObject<Block> RESONATING_PLATE_BLOCK_BLOCK = Registration.BLOCKS.register("resonating_plate_block", () -> new BlockResonatingPlate(Block.Properties.of(Material.STONE)
            .strength(3, 5)));

    public static final RegistryObject<Item> RESONATING_PLATE_ITEM = Registration.ITEMS.register("resonating_plate", tab(() -> new Item(Registration.createStandardProperties())));
    public static final RegistryObject<Item> FILTER_MATERIAL_ITEM = Registration.ITEMS.register("filter_material", tab(() -> new Item(Registration.createStandardProperties())));    // @todo 1.16 ItemWithTooltip)?
    public static final RegistryObject<Item> SPENT_FILTER_ITEM = Registration.ITEMS.register("spent_filter_material", tab(() -> new Item(Registration.createStandardProperties())));
    public static final RegistryObject<Item> LIQUID_INJECTOR_ITEM = Registration.ITEMS.register("liquid_injector", tab(() -> new ItemLiquidInjector(Registration.createStandardProperties())));
    public static final RegistryObject<Item> MACHINE_FRAME_ITEM = Registration.ITEMS.register("machine_frame", tab(() -> new Item(Registration.createStandardProperties())));
    public static final RegistryObject<Item> RESONATING_ORE_DEEPSLATE_ITEM = Registration.fromBlock(RESONATING_ORE_DEEPSLATE_BLOCK);
    public static final RegistryObject<Item> RESONATING_ORE_STONE_ITEM = Registration.fromBlock(RESONATING_ORE_STONE_BLOCK);
    public static final RegistryObject<Item> RESONATING_ORE_NETHER_ITEM = Registration.fromBlock(RESONATING_ORE_NETHER_BLOCK);
    public static final RegistryObject<Item> RESONATING_ORE_END_ITEM = Registration.fromBlock(RESONATING_ORE_END_BLOCK);
    public static final RegistryObject<Item> RESONATING_PLATE_BLOCK_ITEM = Registration.fromBlock(RESONATING_PLATE_BLOCK_BLOCK);

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
    public void initConfig() {
        CrystalConfig.init();
        ResonatingPlateBlockConfig.init();
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.builder(RESONATING_ORE_DEEPSLATE_BLOCK, RESONATING_ORE_DEEPSLATE_ITEM)
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(Tags.Blocks.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(Tags.Items.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
                Dob.builder(RESONATING_ORE_END_BLOCK, RESONATING_ORE_END_ITEM)
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(Tags.Blocks.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(Tags.Items.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
                Dob.builder(RESONATING_ORE_NETHER_BLOCK, RESONATING_ORE_NETHER_ITEM)
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(Tags.Blocks.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(Tags.Items.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
                Dob.builder(RESONATING_ORE_STONE_BLOCK, RESONATING_ORE_STONE_ITEM)
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .blockTags(List.of(Tags.Blocks.ORES, DeepResonanceTags.RESONANT_ORE))
                        .itemTags(List.of(Tags.Items.ORES, DeepResonanceTags.RESONANT_ORE_ITEM)),
                Dob.blockBuilder(RESONATING_PLATE_BLOCK_BLOCK)
                        .simpleLoot()
                        .simpleBlockState()
                        .parentedItem()
                        .diamondPickaxeTags()
                        .shaped(builder -> builder
                                        .unlockedBy("has_resonant_plate", DataGen.has(RESONATING_PLATE_ITEM.get()))
                                        .define('P', RESONATING_PLATE_ITEM.get()),
                                "PPP", "PPP", "PPP"),
                Dob.blockBuilder(RESONATING_CRYSTAL_NATURAL_EMPTY)
                        .standardLoot(TYPE_RESONATING_CRYSTAL)
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_NATURAL_EMPTY, provider, "crystal_empty", "crystal", "empty_crystal");
                        })
                        .parentedItem("block/crystal_empty")
                        .diamondPickaxeTags(),
                Dob.blockBuilder(RESONATING_CRYSTAL_NATURAL)
                        .standardLoot(TYPE_RESONATING_CRYSTAL)
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_NATURAL, provider, "crystal_full", "crystal", "crystal");
                        })
                        .parentedItem("block/crystal_full")
                        .diamondPickaxeTags(),
                Dob.blockBuilder(RESONATING_CRYSTAL_GENERATED_EMPTY)
                        .standardLoot(TYPE_RESONATING_CRYSTAL)
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_GENERATED_EMPTY, provider, "crystal_empty_pure", "crystal_generated", "empty_crystal");
                        })
                        .parentedItem("block/crystal_empty_pure")
                        .diamondPickaxeTags(),
                Dob.blockBuilder(RESONATING_CRYSTAL_GENERATED)
                        .standardLoot(TYPE_RESONATING_CRYSTAL)
                        .blockState(provider -> {
                            DataGenHelper.generateCrystal(RESONATING_CRYSTAL_GENERATED, provider, "crystal_full_pure", "crystal_generated", "crystal");
                        })
                        .parentedItem("block/crystal_full_pure")
                        .diamondPickaxeTags(),
                Dob.itemBuilder(FILTER_MATERIAL_ITEM)
                        .generatedItem("item/filter_material")
                        .shaped(builder -> builder
                                        .define('g', Tags.Items.GRAVEL)
                                        .define('s', ItemTags.SAND)
                                        .unlockedBy("has_gravel", DataGen.inventoryTrigger(ItemPredicate.Builder.item().of(Tags.Items.GRAVEL).build())),
                                8,
                                "gcg", "csc", "gcg"),
                Dob.itemBuilder(LIQUID_INJECTOR_ITEM)
                        .generatedItem("item/liquid_injector"),
                Dob.itemBuilder(RESONATING_PLATE_ITEM)
                        .generatedItem("item/resonating_plate"),
                Dob.itemBuilder(SPENT_FILTER_ITEM)
                        .generatedItem("item/spent_filter_material"),
                Dob.itemBuilder(MACHINE_FRAME_ITEM)
                        .cubeAll(new ResourceLocation(DeepResonance.MODID, "block/machine_side"))
                        .shaped(builder -> builder
                                        .define('g', Tags.Items.STONE)
                                        .define('P', RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_iron", DataGen.inventoryTrigger(ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build())),
                                "iPi", "PgP", "iPi")
        );

    }
}
