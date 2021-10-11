package mcjty.deepresonance.modules.machines;

import mcjty.deepresonance.api.laser.ILens;
import mcjty.deepresonance.api.laser.ILensMirror;
import mcjty.deepresonance.modules.machines.block.*;
import mcjty.deepresonance.modules.machines.client.ClientSetup;
import mcjty.deepresonance.modules.machines.client.CrystallizerTESR;
import mcjty.deepresonance.modules.machines.client.LaserTESR;
import mcjty.deepresonance.modules.machines.item.ItemLens;
import mcjty.deepresonance.modules.machines.util.InfusionBonusRegistry;
import mcjty.deepresonance.modules.machines.util.config.*;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.deepresonance.setup.Registration.CONTAINERS;
import static mcjty.deepresonance.setup.Registration.TILES;

public class MachinesModule implements IModule {

    public static final RegistryObject<Block> VALVE_BLOCK = Registration.BLOCKS.register("valve", ValveTileEntity::createBlock);
    public static final RegistryObject<Item> VALVE_ITEM = Registration.fromBlock(VALVE_BLOCK);
    public static final RegistryObject<TileEntityType<ValveTileEntity>> TYPE_VALVE = TILES.register("valve", () -> TileEntityType.Builder.of(ValveTileEntity::new, VALVE_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> VALVE_CONTAINER = CONTAINERS.register("valve", GenericContainer::createContainerType);

    public static final RegistryObject<Block> SMELTER_BLOCK = Registration.BLOCKS.register("smelter", SmelterTileEntity::createBlock);
    public static final RegistryObject<Item> SMELTER_ITEM = Registration.fromBlock(SMELTER_BLOCK);
    public static final RegistryObject<TileEntityType<SmelterTileEntity>> TYPE_SMELTER = TILES.register("smelter", () -> TileEntityType.Builder.of(SmelterTileEntity::new, SMELTER_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> SMELTER_CONTAINER = CONTAINERS.register("smelter", GenericContainer::createContainerType);

    public static final RegistryObject<Block> PURIFIER_BLOCK = Registration.BLOCKS.register("purifier", PurifierTileEntity::createBlock);
    public static final RegistryObject<Item> PURIFIER_ITEM = Registration.fromBlock(PURIFIER_BLOCK);
    public static final RegistryObject<TileEntityType<PurifierTileEntity>> TYPE_PURIFIER = TILES.register("purifier", () -> TileEntityType.Builder.of(PurifierTileEntity::new, PURIFIER_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> PURIFIER_CONTAINER = CONTAINERS.register("purifier", GenericContainer::createContainerType);

    public static final RegistryObject<LensBlock> LENS_BLOCK = Registration.BLOCKS.register("lens", () -> new LensBlock(Block.Properties.of(Material.METAL).strength(2.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Item> LENS_ITEM = Registration.ITEMS.register("lens", () -> new ItemLens(LENS_BLOCK.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<LensTileEntity>> TYPE_LENS = TILES.register("lens", () -> TileEntityType.Builder.of(LensTileEntity::new, LENS_BLOCK.get()).build(null));

    public static final RegistryObject<Block> LASER_BLOCK = Registration.BLOCKS.register("laser", LaserTileEntity::createBlock);
    public static final RegistryObject<Item> LASER_ITEM = Registration.fromBlock(LASER_BLOCK);
    public static final RegistryObject<TileEntityType<LaserTileEntity>> TYPE_LASER = TILES.register("laser", () -> TileEntityType.Builder.of(LaserTileEntity::new, LASER_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> LASER_CONTAINER = CONTAINERS.register("laser", GenericContainer::createContainerType);

    public static final RegistryObject<Block> CRYSTALLIZER_BLOCK = Registration.BLOCKS.register("crystallizer", CrystallizerTileEntity::createBlock);
    public static final RegistryObject<Item> CRYSTALLIZER_ITEM = Registration.fromBlock(CRYSTALLIZER_BLOCK);
    public static final RegistryObject<TileEntityType<CrystallizerTileEntity>> TYPE_CRYSTALIZER = TILES.register("crystallizer", () -> TileEntityType.Builder.of(CrystallizerTileEntity::new, CRYSTALLIZER_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CRYSTALIZER_CONTAINER = CONTAINERS.register("crystallizer", GenericContainer::createContainerType);

    public static final InfusionBonusRegistry INFUSION_BONUSES = new InfusionBonusRegistry();

    @CapabilityInject(ILens.class)
    public static Capability<ILens> LENS_CAPABILITY;

    @CapabilityInject(ILensMirror.class)
    public static Capability<ILensMirror> LENS_MIRROR_CAPABILITY;

    public static CrystallizerConfig crystallizerConfig;
    public static LaserConfig laserConfig;
    public static PurifierConfig purifierConfig;
    public static SmelterConfig smelterConfig;
    public static ValveConfig valveConfig;

    public MachinesModule() {
        // @todo 1.16
//        RegistryHelper.registerEmptyCapability(ILens.class);
//        RegistryHelper.registerEmptyCapability(ILensMirror.class);
//        SubTileRegistry.INSTANCE.registerSubTile(SubTileLens.class, new ResourceLocation(DeepResonance.MODID, "lens"));
//        SubTileRegistry.INSTANCE.registerSubTile(SubTileLensMirror.class, new ResourceLocation(DeepResonance.MODID, "lens_mirror"));
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadModels);
    }

    // @todo 1.16
//    private void loadModels(ModelLoadEvent event) {
//        ModelResourceLocation location = new ModelResourceLocation(new ResourceLocation(DeepResonance.MODID, "lens"), "");
//        event.registerModel(location, LensModelCache.INSTANCE.setModel(event.getModel(location)));
//        location = new ModelResourceLocation(new ResourceLocation(DeepResonance.MODID, "resonating_crystal_model"), "empty=false,facing=north,very_pure=true");
//        CrystallizerTESR.setModel(Preconditions.checkNotNull(event.getModel(location)));
//    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        ClientSetup.initClient();

        CrystallizerTESR.register();
        LaserTESR.register();

        event.enqueueWork(() -> {
            ClientSetup.setupBlockColors();
        });
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initConfig() {
        // @todo 1.16
//        Config.configuration.configureSubConfig("machines", "Machines module settings", config -> {
//            crystallizerConfig = config.registerConfig(CrystallizerConfig::new, "crystallizer", "Crystallizer settings");
//            laserConfig = config.registerConfig(LaserConfig::new, "laser", "Laser settings");
//            purifierConfig = config.registerConfig(PurifierConfig::new, "purifier", "Purifier settings");
//            smelterConfig = config.registerConfig(SmelterConfig::new, "smelter", "Smelter settings");
//            valveConfig = config.registerConfig(ValveConfig::new, "valve", "Valve settings");
//        });
    }
}
