package mcjty.deepresonance.modules.core;

import mcjty.deepresonance.modules.core.block.BlockCrystal;
import mcjty.deepresonance.modules.core.block.BlockResonatingPlate;
import mcjty.deepresonance.modules.core.client.ClientSetup;
import mcjty.deepresonance.modules.core.fluid.FluidLiquidCrystal;
import mcjty.deepresonance.modules.core.item.ItemLiquidInjector;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
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

/**
 * Created by Elec332 on 10-1-2020
 */
public class CoreModule implements IModule {

    public static final String TILE_DATA_TAG = "tileDataTag";   // @todo 1.16 change this!

    private static final Block.Properties ORE_PROPERTIES = Block.Properties.of(Material.ROCK).hardnessAndResistance(3, 5).harvestLevel(3).harvestTool(ToolType.PICKAXE);

    public static final RegistryObject<Fluid> LIQUID_CRYSTAL = Registration.FLUIDS.register("liquid_crystal", FluidLiquidCrystal::new);

    public static final RegistryObject<BlockCrystal> RESONATING_CRYSTAL_BLOCK = Registration.BLOCKS.register("resonating_crystal", BlockCrystal::new);
    public static final RegistryObject<TileEntityType<TileEntityResonatingCrystal>> TYPE_RESONATING_CRYSTAL = TILES.register("resonating_crystal", () -> TileEntityType.Builder.create(TileEntityResonatingCrystal::new, RESONATING_CRYSTAL_BLOCK.get()).build(null));

    public static final RegistryObject<Block> RESONATING_ORE_STONE_BLOCK = Registration.BLOCKS.register("resonating_ore_stone", () -> new Block(ORE_PROPERTIES));
    public static final RegistryObject<Block> RESONATING_ORE_NETHER_BLOCK = Registration.BLOCKS.register("resonating_ore_nether", () -> new Block(ORE_PROPERTIES));
    public static final RegistryObject<Block> RESONATING_ORE_END_BLOCK = Registration.BLOCKS.register("resonating_ore_end", () -> new Block(ORE_PROPERTIES));
    public static final RegistryObject<Block> RESONATING_PLATE_BLOCK_BLOCK = Registration.BLOCKS.register("resonating_plate_block", () -> new BlockResonatingPlate(Block.Properties.create(Material.ROCK).hardnessAndResistance(3, 5).harvestLevel(2).harvestTool(ToolType.PICKAXE)));

    public static final RegistryObject<Item> RESONATING_PLATE_ITEM = Registration.ITEMS.register("resonating_plate", () -> new Item(Registration.createStandardProperties()));
    public static final RegistryObject<Item> FILTER_MATERIAL_ITEM = Registration.ITEMS.register("filter_material", () -> new ItemWithTooltip(Registration.createStandardProperties()));
    public static final RegistryObject<Item> SPENT_FILTER_ITEM = Registration.ITEMS.register("spent_filter_material", () -> new Item(Registration.createStandardProperties()));
    public static final RegistryObject<Item> LIQUID_INJECTOR_ITEM = Registration.ITEMS.register("liquid_injector", () -> new ItemLiquidInjector(Registration.createStandardProperties()));
    public static final RegistryObject<Item> MACHINE_FRAME_ITEM = Registration.ITEMS.register("machine_frame", () -> new Item(Registration.createStandardProperties()));
    public static final RegistryObject<Item> RESONATING_CRYSTAL_ITEM = Registration.fromBlock(RESONATING_CRYSTAL_BLOCK);
    public static final RegistryObject<Item> RESONATING_ORE_STONE_ITEM = Registration.fromBlock(RESONATING_ORE_STONE_BLOCK);
    public static final RegistryObject<Item> RESONATING_ORE_NETHER_ITEM = Registration.fromBlock(RESONATING_ORE_NETHER_BLOCK);
    public static final RegistryObject<Item> RESONATING_ORE_END_ITEM = Registration.fromBlock(RESONATING_ORE_END_BLOCK);
    public static final RegistryObject<Item> RESONATING_PLATE_BLOCK_ITEM = Registration.fromBlock(RESONATING_PLATE_BLOCK_BLOCK);

    public static CrystalConfig crystalConfig;
    public static ResonatingPlateBlockConfig resonatingPlateConfig;

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
    }

    @Override
    public void initConfig() {
        // @todo 1.16
//        Config.configuration.configureSubConfig("core", "Core module settings", config -> {
//            crystalConfig = config.registerConfig(CrystalConfig::new, "resonating_crystal", "Resonating Crystal settings");
//            resonatingPlateConfig = config.registerConfig(ResonatingPlateBlockConfig::new, "resonating_plate_block", "Resonating Plate Block settings");
//        });
    }
}
