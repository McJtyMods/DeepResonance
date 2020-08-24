package mcjty.deepresonance.modules.tank;

import com.google.common.base.Preconditions;
import elec332.core.api.client.model.ModelLoadEvent;
import elec332.core.api.config.IConfigurableElement;
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
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 8-1-2020
 */
public class TankModule implements IConfigurableElement {

    public static final RegistryObject<Block> TANK_BLOCK = DeepResonance.BLOCKS.register("tank", BlockTank::new);
    public static final RegistryObject<Item> TANK_ITEM = DeepResonance.ITEMS.register("tank", () -> new BlockItem(Preconditions.checkNotNull(TANK_BLOCK.get()), DeepResonance.createStandardProperties()));

    public static ForgeConfigSpec.BooleanValue quickRender;

    public TankModule(IEventBus eventBus) {
        DeepResonance.configuration.registerConfigurableElement(this);
        DeepResonance.clientConfiguration.registerConfigurableElement(this);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::loadModels);

        RegistryHelper.registerTileEntityLater(TileEntityTank.class, new DeepResonanceResourceLocation("tank"));
    }

    private void setup(FMLCommonSetupEvent event) {
        DeepResonance.logger.info("Registering tank grid handler");
        ElecCoreRegistrar.GRIDHANDLERS.register(new TankGridHandler());
    }

    private void clientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.instance().registerLoader(TankRenderer.INSTANCE);
        RenderingRegistry.instance().setItemRenderer(TANK_ITEM.get(), new TankItemRenderer());
        RenderTypeLookup.setRenderLayer(TankModule.TANK_BLOCK.get(), RenderType.getTranslucent());
        RenderHelper.registerTESR(TileEntityTank.class, new TankTESR());
    }

    private void loadModels(ModelLoadEvent event) {
        ModelResourceLocation location = new ModelResourceLocation(new DeepResonanceResourceLocation("tank"), "");
        event.registerModel(location, TankRenderer.INSTANCE.setModel(event.getModel(location)));
    }

    @Override
    public void registerProperties(@Nonnull ForgeConfigSpec.Builder config, ModConfig.Type type) {
        config.comment("Tank settings").push("tanks");

        if (type == ModConfig.Type.CLIENT) {
            quickRender = config.comment("Whether to use the fast renderer or the fancy one when rendering the inside of tanks.").define("fastRenderer", false);
        }

        config.pop();
    }

}
