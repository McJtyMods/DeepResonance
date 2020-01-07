package mcjty.deepresonance;

import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.registration.IObjectRegister;
import elec332.core.api.registration.IWorldGenRegister;
import elec332.core.util.FMLHelper;
import mcjty.deepresonance.setup.*;
import mcjty.lib.base.ModBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 6-1-2020
 */
@Mod(DeepResonance.MODID)
public class DeepResonance implements ModBase, IElecCoreMod {

    public DeepResonance() {
        if (instance != null) {
            throw new RuntimeException();
        }
        instance = this;
        logger = LogManager.getLogger(MODNAME);
        setup = new ModSetup();

        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> setup.init(event));
    }

    public static final String MODID = "deepresonance";
    public static final String MODNAME = FMLHelper.getModNameEarly(MODID);

    public static String SHIFT_MESSAGE = "<Press Shift>"; //Todo: move to McJtyLib and localize from there

    public static DeepResonance instance;
    public static ModSetup setup;
    public static Logger logger;

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(PlayerEntity playerEntity, int i, String s) {
    }

    public static Item.Properties createStandardProperties() {
        return new Item.Properties().group(setup.getTab());
    }

    @Override
    public void registerRegisters(Consumer<IObjectRegister<?>> objectHandler, Consumer<IWorldGenRegister> worldHandler) {
        objectHandler.accept(new ItemRegister());
        objectHandler.accept(new BlockRegister());
        objectHandler.accept(new TileEntityRegister());
        objectHandler.accept(new FluidRegister());
    }

}
