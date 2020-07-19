package mcjty.deepresonance;

import com.google.common.base.Preconditions;
import elec332.core.api.config.IConfigWrapper;
import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.module.IModuleController;
import elec332.core.api.registration.IObjectRegister;
import elec332.core.api.registration.IWorldGenRegister;
import elec332.core.config.ConfigWrapper;
import elec332.core.util.FMLHelper;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.setup.Config;
import mcjty.deepresonance.setup.FluidRegister;
import mcjty.deepresonance.setup.ModSetup;
import mcjty.lib.base.ModBase;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 6-1-2020
 */
@Mod(DeepResonance.MODID)
public class DeepResonance implements ModBase, IElecCoreMod, IModuleController {

    public static final String MODID = "deepresonance";
    public static final String MODNAME = FMLHelper.getModNameEarly(MODID);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MODID);

    public static String SHIFT_MESSAGE = "message.rftoolsbase.shiftmessage";

    public static DeepResonance instance;
    public static IConfigWrapper config, clientConfig;
    public static ModSetup setup;
    public static Logger logger;

    public DeepResonance() {
        if (instance != null) {
            throw new RuntimeException();
        }
        instance = this;
        logger = LogManager.getLogger(MODNAME);
        setup = new ModSetup();
        config = new ConfigWrapper(FMLHelper.getActiveModContainer());
        clientConfig = new ConfigWrapper(FMLHelper.getActiveModContainer(), ModConfig.Type.CLIENT);
        config.registerConfigurableElement(new Config());

        IEventBus modBus = FMLHelper.getActiveModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        FLUIDS.register(modBus);
        modBus.addListener(setup::init);
        modBus.addListener(setup::clientSetup);
        modBus.addListener(new Consumer<FMLLoadCompleteEvent>() {

            @Override
            public void accept(FMLLoadCompleteEvent event) {
                RenderTypeLookup.setRenderLayer(TankModule.TANK_BLOCK.get(), RenderType.getTranslucent());
                RenderTypeLookup.setRenderLayer(CoreModule.RESONATING_CRYSTAL_BLOCK.get(), RenderType.getTranslucent());
            }
        });
    }

    public static Item.Properties createStandardProperties() {
        return new Item.Properties().group(setup.getTab());
    }

    @Override
    public void afterConstruction() {
        config.register();
        clientConfig.register();
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void registerRegisters(Consumer<IObjectRegister<?>> objectHandler, Consumer<IWorldGenRegister> worldHandler) {
        objectHandler.accept(new FluidRegister());
    }

    @Override
    public boolean isModuleEnabled(String moduleName) {
        return true;
    }

    @Override
    public ForgeConfigSpec.BooleanValue getModuleConfig(String moduleName) {
        return config.registerConfig(builder -> builder.define(moduleName.toLowerCase() + ".enabled", true));
    }

    public static RegistryObject<Item> fromBlock(RegistryObject<Block> block) {
        Preconditions.checkNotNull(block.getId().getPath());
        return DeepResonance.ITEMS.register(block.getId().getPath(), () -> new BlockItem(Preconditions.checkNotNull(block.get()), DeepResonance.createStandardProperties()));
    }

}
