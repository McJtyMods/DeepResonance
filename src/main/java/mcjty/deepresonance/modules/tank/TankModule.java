package mcjty.deepresonance.modules.tank;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.tank.blocks.TankBlock;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.modules.tank.client.ClientSetup;
import mcjty.deepresonance.modules.tank.client.TankTESR;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.modules.IModule;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.deepresonance.setup.Registration.TILES;

public class TankModule implements IModule {

    public static final RegistryObject<Block> TANK_BLOCK = Registration.BLOCKS.register("tank", TankBlock::new);
    public static final RegistryObject<Item> TANK_ITEM = Registration.ITEMS.register("tank", () -> new BlockItem(TANK_BLOCK.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<TankTileEntity>> TYPE_TANK = TILES.register("tank", () -> TileEntityType.Builder.of(TankTileEntity::new, TANK_BLOCK.get()).build(null));

    // @todo 1.16 remove me!
    public static ForgeConfigSpec.BooleanValue quickRender;

    public static final ResourceLocation TANK_SYNC_ID = new ResourceLocation(DeepResonance.MODID, "tanksync");

    public TankModule() {
        // @todo 1.16
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadModels);
    }

    // @todo 1.16
//    private void loadModels(ModelLoadEvent event) {
//        ModelResourceLocation location = new ModelResourceLocation(new ResourceLocation(DeepResonance.MODID, "tank"), "");
//        event.registerModel(location, TankRenderer.INSTANCE.setModel(event.getModel(location)));
//    }

    @Override
    public void init(FMLCommonSetupEvent event) {
//        McJtyLib.SYNCER.registerPositionalDataFactory(TANK_SYNC_ID, ClientTankData::fromBytes);
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        ClientSetup.initClient();
        TankTESR.register();
//        McJtyLib.SYNCER.registerClientHandler(TANK_SYNC_ID, ClientTankData::handleClientData);
    }

    @Override
    public void initConfig() {
        // @todo 1.16
//        Config.configuration.registerConfigurableElement((config, type) -> {
//            config.comment("Tank settings").push("tanks");
//            config.pop();
//        });
//        Config.clientConfiguration.registerConfigurableElement((config, type) -> {
//            config.comment("Tank settings").push("tanks");
//            quickRender = config.comment("Whether to use the fast renderer or the fancy one when rendering the inside of tanks.").define("fastRenderer", false);
//            config.pop();
//        });
    }
}
