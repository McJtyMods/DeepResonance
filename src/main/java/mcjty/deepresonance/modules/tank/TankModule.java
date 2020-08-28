package mcjty.deepresonance.modules.tank;

import com.google.common.base.Preconditions;
import elec332.core.api.client.model.ModelLoadEvent;
import elec332.core.client.RenderHelper;
import elec332.core.handler.ElecCoreRegistrar;
import elec332.core.loader.client.RenderingRegistry;
import elec332.core.util.RegistryHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.tank.blocks.BlockTank;
import mcjty.deepresonance.modules.tank.client.TankItemRenderer;
import mcjty.deepresonance.modules.tank.client.TankRenderer;
import mcjty.deepresonance.modules.tank.client.TankTESR;
import mcjty.deepresonance.modules.tank.grid.TankGridHandler;
import mcjty.deepresonance.modules.tank.tile.TileEntityTank;
import mcjty.deepresonance.setup.Config;
import mcjty.deepresonance.setup.Registration;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import mcjty.lib.modules.IModule;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Created by Elec332 on 8-1-2020
 */
public class TankModule implements IModule {

    public static final RegistryObject<Block> TANK_BLOCK = Registration.BLOCKS.register("tank", BlockTank::new);
    public static final RegistryObject<Item> TANK_ITEM = Registration.ITEMS.register("tank", () -> new BlockItem(Preconditions.checkNotNull(TANK_BLOCK.get()), Registration.createStandardProperties()));

    public static ForgeConfigSpec.BooleanValue quickRender;

    public TankModule() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadModels);

        RegistryHelper.registerTileEntityLater(TileEntityTank.class, new DeepResonanceResourceLocation("tank"));
    }

    private void loadModels(ModelLoadEvent event) {
        ModelResourceLocation location = new ModelResourceLocation(new DeepResonanceResourceLocation("tank"), "");
        event.registerModel(location, TankRenderer.INSTANCE.setModel(event.getModel(location)));
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        DeepResonance.logger.info("Registering tank grid handler");
        ElecCoreRegistrar.GRIDHANDLERS.register(new TankGridHandler());
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        RenderingRegistry.instance().registerLoader(TankRenderer.INSTANCE);
        RenderingRegistry.instance().setItemRenderer(TANK_ITEM.get(), new TankItemRenderer());
        RenderTypeLookup.setRenderLayer(TankModule.TANK_BLOCK.get(), RenderType.getTranslucent());
        RenderHelper.registerTESR(TileEntityTank.class, new TankTESR());
    }

    @Override
    public void initConfig() {
        Config.configuration.registerConfigurableElement((config, type) -> {
            config.comment("Tank settings").push("tanks");
            config.pop();
        });
        Config.clientConfiguration.registerConfigurableElement((config, type) -> {
            config.comment("Tank settings").push("tanks");
            quickRender = config.comment("Whether to use the fast renderer or the fancy one when rendering the inside of tanks.").define("fastRenderer", false);
            config.pop();
        });
    }
}