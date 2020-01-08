package mcjty.deepresonance;

import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.registration.IObjectRegister;
import elec332.core.api.registration.IWorldGenRegister;
import elec332.core.util.FMLHelper;
import mcjty.deepresonance.setup.FluidRegister;
import mcjty.deepresonance.setup.ModSetup;
import mcjty.lib.base.ModBase;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 6-1-2020
 */
@Mod(DeepResonance.MODID)
public class DeepResonance implements ModBase, IElecCoreMod {

    public static final String MODID = "deepresonance";
    public static final String MODNAME = FMLHelper.getModNameEarly(MODID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, MODID);
    public static String SHIFT_MESSAGE = "<Press Shift>"; //Todo: move to McJtyLib and localize from there
    public static DeepResonance instance;
    public static ModSetup setup;
    public static Logger logger;

    public DeepResonance() {
        if (instance != null) {
            throw new RuntimeException();
        }
        instance = this;
        logger = LogManager.getLogger(MODNAME);
        setup = new ModSetup();

        IEventBus modBus = FMLHelper.getActiveModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        FLUIDS.register(modBus);
        modBus.addListener((FMLCommonSetupEvent event) -> setup.init(event));
    }

    public static Item.Properties createStandardProperties() {
        return new Item.Properties().group(setup.getTab());
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(PlayerEntity playerEntity, int i, String s) {
    }

    @Override
    public void registerRegisters(Consumer<IObjectRegister<?>> objectHandler, Consumer<IWorldGenRegister> worldHandler) {
        objectHandler.accept(new FluidRegister());
    }

}
