package mcjty.deepresonance.modules.core;

import mcjty.deepresonance.modules.core.block.BlockResonatingPlate;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalBlock;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.core.client.ClientSetup;
import mcjty.deepresonance.modules.core.client.ResonatingCrystalRenderer;
import mcjty.deepresonance.modules.core.fluid.FluidLiquidCrystal;
import mcjty.deepresonance.modules.core.item.ItemLiquidInjector;
import mcjty.deepresonance.modules.core.util.CrystalConfig;
import mcjty.deepresonance.modules.core.util.ResonatingPlateBlockConfig;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.modules.IModule;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.deepresonance.setup.Registration.TILES;

public class CoreModule implements IModule {

    public static final String TILE_DATA_TAG = "BlockEntityTag";

    private static final Block.Properties ORE_PROPERTIES = Block.Properties.of(Material.STONE).strength(3, 5).harvestLevel(3).harvestTool(ToolType.PICKAXE);

    public static final RegistryObject<Fluid> LIQUID_CRYSTAL = Registration.FLUIDS.register("liquid_crystal", FluidLiquidCrystal::new);

    public static final RegistryObject<ResonatingCrystalBlock> RESONATING_CRYSTAL_NATURAL = Registration.BLOCKS.register("resonating_crystal_natural", () -> new ResonatingCrystalBlock(false, false));
    public static final RegistryObject<ResonatingCrystalBlock> RESONATING_CRYSTAL_NATURAL_EMPTY = Registration.BLOCKS.register("resonating_crystal_natural_empty", () -> new ResonatingCrystalBlock(false, true));
    public static final RegistryObject<ResonatingCrystalBlock> RESONATING_CRYSTAL_GENERATED = Registration.BLOCKS.register("resonating_crystal_generated", () -> new ResonatingCrystalBlock(true, false));
    public static final RegistryObject<ResonatingCrystalBlock> RESONATING_CRYSTAL_GENERATED_EMPTY = Registration.BLOCKS.register("resonating_crystal_generated_empty", () -> new ResonatingCrystalBlock(true, true));
    public static final RegistryObject<Item> RESONATING_CRYSTAL_NATURAL_ITEM = Registration.fromBlock(RESONATING_CRYSTAL_NATURAL);
    public static final RegistryObject<Item> RESONATING_CRYSTAL_NATURAL_EMPTY_ITEM = Registration.fromBlock(RESONATING_CRYSTAL_NATURAL_EMPTY);
    public static final RegistryObject<Item> RESONATING_CRYSTAL_GENERATED_ITEM = Registration.fromBlock(RESONATING_CRYSTAL_GENERATED);
    public static final RegistryObject<Item> RESONATING_CRYSTAL_GENERATED_EMPTY_ITEM = Registration.fromBlock(RESONATING_CRYSTAL_GENERATED_EMPTY);
    public static final RegistryObject<TileEntityType<ResonatingCrystalTileEntity>> TYPE_RESONATING_CRYSTAL = TILES.register("resonating_crystal", () -> TileEntityType.Builder.of(ResonatingCrystalTileEntity::new,
            RESONATING_CRYSTAL_GENERATED.get(), RESONATING_CRYSTAL_GENERATED_EMPTY.get(),
            RESONATING_CRYSTAL_NATURAL.get(), RESONATING_CRYSTAL_NATURAL_EMPTY.get())
            .build(null));

    public static final RegistryObject<Block> RESONATING_ORE_STONE_BLOCK = Registration.BLOCKS.register("resonating_ore_stone", () -> new Block(ORE_PROPERTIES));
    public static final RegistryObject<Block> RESONATING_ORE_NETHER_BLOCK = Registration.BLOCKS.register("resonating_ore_nether", () -> new Block(ORE_PROPERTIES));
    public static final RegistryObject<Block> RESONATING_ORE_END_BLOCK = Registration.BLOCKS.register("resonating_ore_end", () -> new Block(ORE_PROPERTIES));
    public static final RegistryObject<Block> RESONATING_PLATE_BLOCK_BLOCK = Registration.BLOCKS.register("resonating_plate_block", () -> new BlockResonatingPlate(Block.Properties.of(Material.STONE).strength(3, 5).harvestLevel(2).harvestTool(ToolType.PICKAXE)));

    public static final RegistryObject<Item> RESONATING_PLATE_ITEM = Registration.ITEMS.register("resonating_plate", () -> new Item(Registration.createStandardProperties()));
    public static final RegistryObject<Item> FILTER_MATERIAL_ITEM = Registration.ITEMS.register("filter_material", () -> new Item(Registration.createStandardProperties()));    // @todo 1.16 ItemWithTooltip?
    public static final RegistryObject<Item> SPENT_FILTER_ITEM = Registration.ITEMS.register("spent_filter_material", () -> new Item(Registration.createStandardProperties()));
    public static final RegistryObject<Item> LIQUID_INJECTOR_ITEM = Registration.ITEMS.register("liquid_injector", () -> new ItemLiquidInjector(Registration.createStandardProperties()));
    public static final RegistryObject<Item> MACHINE_FRAME_ITEM = Registration.ITEMS.register("machine_frame", () -> new Item(Registration.createStandardProperties()));
    public static final RegistryObject<Item> RESONATING_ORE_STONE_ITEM = Registration.fromBlock(RESONATING_ORE_STONE_BLOCK);
    public static final RegistryObject<Item> RESONATING_ORE_NETHER_ITEM = Registration.fromBlock(RESONATING_ORE_NETHER_BLOCK);
    public static final RegistryObject<Item> RESONATING_ORE_END_ITEM = Registration.fromBlock(RESONATING_ORE_END_BLOCK);
    public static final RegistryObject<Item> RESONATING_PLATE_BLOCK_ITEM = Registration.fromBlock(RESONATING_PLATE_BLOCK_BLOCK);

    public CoreModule() {
        // @todo 1.16
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IModelManager.class, this::registerModelLoader);
    }

    // @todo 1.16
//    private void registerModelLoader(APIInjectedEvent<IModelManager> event) {
//        event.getInjectedAPI().registerModelHandler(new ModelLoaderCoreModule());
//    }

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        ClientSetup.initClient();
        ResonatingCrystalRenderer.register();
    }

    @Override
    public void initConfig() {
        CrystalConfig.init();
        ResonatingPlateBlockConfig.init();
    }
}
