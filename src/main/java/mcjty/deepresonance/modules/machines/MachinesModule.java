package mcjty.deepresonance.modules.machines;

import mcjty.deepresonance.modules.machines.block.*;
import mcjty.deepresonance.modules.machines.client.*;
import mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry;
import mcjty.deepresonance.modules.machines.item.ItemLens;
import mcjty.deepresonance.modules.machines.util.config.*;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.deepresonance.setup.Registration.CONTAINERS;
import static mcjty.deepresonance.setup.Registration.TILES;

public class MachinesModule implements IModule {

    public static final RegistryObject<Block> VALVE_BLOCK = Registration.BLOCKS.register("valve", ValveTileEntity::createBlock);
    public static final RegistryObject<Item> VALVE_ITEM = Registration.fromBlock(VALVE_BLOCK);
    public static final RegistryObject<BlockEntityType<ValveTileEntity>> TYPE_VALVE = TILES.register("valve", () -> BlockEntityType.Builder.of(ValveTileEntity::new, VALVE_BLOCK.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> VALVE_CONTAINER = CONTAINERS.register("valve", GenericContainer::createContainerType);

    public static final RegistryObject<Block> SMELTER_BLOCK = Registration.BLOCKS.register("smelter", SmelterTileEntity::createBlock);
    public static final RegistryObject<Item> SMELTER_ITEM = Registration.fromBlock(SMELTER_BLOCK);
    public static final RegistryObject<BlockEntityType<SmelterTileEntity>> TYPE_SMELTER = TILES.register("smelter", () -> BlockEntityType.Builder.of(SmelterTileEntity::new, SMELTER_BLOCK.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> SMELTER_CONTAINER = CONTAINERS.register("smelter", GenericContainer::createContainerType);

    public static final RegistryObject<Block> PURIFIER_BLOCK = Registration.BLOCKS.register("purifier", PurifierTileEntity::createBlock);
    public static final RegistryObject<Item> PURIFIER_ITEM = Registration.fromBlock(PURIFIER_BLOCK);
    public static final RegistryObject<BlockEntityType<PurifierTileEntity>> TYPE_PURIFIER = TILES.register("purifier", () -> BlockEntityType.Builder.of(PurifierTileEntity::new, PURIFIER_BLOCK.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> PURIFIER_CONTAINER = CONTAINERS.register("purifier", GenericContainer::createContainerType);

    public static final RegistryObject<LensBlock> LENS_BLOCK = Registration.BLOCKS.register("lens", LensBlock::new);
    public static final RegistryObject<Item> LENS_ITEM = Registration.ITEMS.register("lens", () -> new ItemLens(LENS_BLOCK.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<LensTileEntity>> TYPE_LENS = TILES.register("lens", () -> BlockEntityType.Builder.of(LensTileEntity::new, LENS_BLOCK.get()).build(null));

    public static final RegistryObject<Block> LASER_BLOCK = Registration.BLOCKS.register("laser", LaserTileEntity::createBlock);
    public static final RegistryObject<Item> LASER_ITEM = Registration.fromBlock(LASER_BLOCK);
    public static final RegistryObject<BlockEntityType<LaserTileEntity>> TYPE_LASER = TILES.register("laser", () -> BlockEntityType.Builder.of(LaserTileEntity::new, LASER_BLOCK.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> LASER_CONTAINER = CONTAINERS.register("laser", GenericContainer::createContainerType);

    public static final RegistryObject<Block> CRYSTALLIZER_BLOCK = Registration.BLOCKS.register("crystallizer", CrystallizerTileEntity::createBlock);
    public static final RegistryObject<Item> CRYSTALLIZER_ITEM = Registration.fromBlock(CRYSTALLIZER_BLOCK);
    public static final RegistryObject<BlockEntityType<CrystallizerTileEntity>> TYPE_CRYSTALIZER = TILES.register("crystallizer", () -> BlockEntityType.Builder.of(CrystallizerTileEntity::new, CRYSTALLIZER_BLOCK.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CRYSTALIZER_CONTAINER = CONTAINERS.register("crystallizer", GenericContainer::createContainerType);

    public MachinesModule() {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        ClientSetup.initClient();

        CrystallizerRenderer.register();
        LaserRenderer.register();

        event.enqueueWork(() -> {
            SmelterGui.register();
            PurifierGui.register();
            LaserGui.register();
            ValveGui.register();
            CrystallizerGui.register();
        });
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        InfusionBonusRegistry.createDefaultInfusionBonusMap();
    }

    @Override
    public void initConfig() {
        CrystallizerConfig.init();
        LaserConfig.init();
        PurifierConfig.init();
        SmelterConfig.init();
        ValveConfig.init();
    }
}
