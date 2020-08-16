package mcjty.deepresonance;

import com.google.common.base.Preconditions;
import elec332.core.api.config.IConfigWrapper;
import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.module.IModuleController;
import elec332.core.api.module.IModuleInfo;
import elec332.core.config.ConfigWrapper;
import elec332.core.util.FMLHelper;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.pulser.PulserModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.worldgen.WorldGenModule;
import mcjty.deepresonance.setup.ModSetup;
import mcjty.deepresonance.util.DeepResonanceBlock;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.McJtyLib;
import mcjty.lib.base.ModBase;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
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

import java.util.function.*;

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
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public static String SHIFT_MESSAGE = "message.rftoolsbase.shiftmessage";

    public static DeepResonance instance;
    public static IConfigWrapper configuration, clientConfiguration;
    public static ModSetup setup;
    public static Logger logger;

    public DeepResonance() {
        if (instance != null) {
            throw new RuntimeException();
        }
        instance = this;
        logger = LogManager.getLogger(MODNAME);
        setup = new ModSetup();
        configuration = new ConfigWrapper(FMLHelper.getActiveModContainer());
        clientConfiguration = new ConfigWrapper(FMLHelper.getActiveModContainer(), ModConfig.Type.CLIENT);

        IEventBus modBus = FMLHelper.getActiveModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        FLUIDS.register(modBus);
        CONTAINERS.register(modBus);
        modBus.addListener(setup::init);
        modBus.addListener(new Consumer<FMLLoadCompleteEvent>() {

            @Override
            public void accept(FMLLoadCompleteEvent event) { //Todo: Pull for McJtyLib
                PacketHandler.registerStandardMessages(9, McJtyLib.networkHandler);
            }

        });
    }

    @Override
    public void afterConstruction() {
        configuration.register();
        clientConfiguration.register();
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public boolean isModuleEnabled(String moduleName) {
        return true;
    }

    @Override
    public void registerAdditionalModules(Consumer<IModuleInfo> registry, BiFunction<String, Class<?>, IModuleInfo.Builder> factory1, BiFunction<String, String, IModuleInfo.Builder> factory2) {
        registry.accept(factory1.apply("Core", CoreModule.class).alwaysEnabled().build());
        registry.accept(factory1.apply("Generator", GeneratorModule.class).build());
        registry.accept(factory1.apply("Machines", MachinesModule.class).build());
        registry.accept(factory1.apply("Pulser", PulserModule.class).build());
        registry.accept(factory1.apply("Radiation", RadiationModule.class).build());
        registry.accept(factory1.apply("Tanks", TankModule.class).build());
        registry.accept(factory1.apply("WorldGen", WorldGenModule.class).build());
    }

    @Override
    public ForgeConfigSpec.BooleanValue getModuleConfig(String moduleName) {
        return configuration.registerConfig(builder -> builder.comment("Whether the " + moduleName.toLowerCase() + " should be enabled").define(moduleName.toLowerCase() + ".enabled", true));
    }

    public static Item.Properties createStandardProperties() {
        return new Item.Properties().group(setup.getTab());
    }

    public static RegistryObject<Block> defaultBlock(final String name, final Supplier<TileEntity> tile) {
        return defaultBlock(name, tile, null);
    }

    public static RegistryObject<Block> defaultBlock(final String name, final Supplier<TileEntity> tile, UnaryOperator<BlockState> mod, IProperty<?>... props) {
        return block(name, tile, builder -> new DeepResonanceBlock(builder) {

            @Override
            protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
                super.fillStateContainer(builder);
                builder.add(props);
            }

        }.modifyDefaultState(mod));
    }

    public static RegistryObject<Block> nonRotatingBlock(final String name, final Supplier<TileEntity> tile) {
        return nonRotatingBlock(name, tile, null);
    }

    public static RegistryObject<Block> nonRotatingBlock(final String name, final Supplier<TileEntity> tile, UnaryOperator<BlockState> mod, IProperty<?>... props) {
        return block(name, tile, builder -> new DeepResonanceBlock(builder) {

            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }

            @Override
            protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
                builder.add(props);
            }

        }.modifyDefaultState(mod));
    }

    public static RegistryObject<Block> block(final String name, final Supplier<TileEntity> tile, final Function<BlockBuilder, DeepResonanceBlock> constructor) {
        return DeepResonance.BLOCKS.register(name, () -> constructor.apply(new BlockBuilder().tileEntitySupplier(tile).infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey(name)))));
    }

    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        Preconditions.checkNotNull(block.getId().getPath());
        return DeepResonance.ITEMS.register(block.getId().getPath(), () -> new BlockItem(Preconditions.checkNotNull(block.get()), DeepResonance.createStandardProperties()));
    }

}
