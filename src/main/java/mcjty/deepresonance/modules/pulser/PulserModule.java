package mcjty.deepresonance.modules.pulser;

import elec332.core.util.RegistryHelper;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.modules.pulser.tile.TileEntityPulser;
import mcjty.deepresonance.modules.pulser.util.PulserBlockConfig;
import mcjty.deepresonance.modules.pulser.util.PulserCapability;
import mcjty.deepresonance.modules.pulser.util.PulserCrystalConfig;
import mcjty.deepresonance.setup.Config;
import mcjty.deepresonance.setup.Registration;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import mcjty.lib.modules.IModule;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Consumer;

import static mcjty.deepresonance.setup.Registration.TILES;

/**
 * Created by Elec332 on 31-7-2020
 */
public class PulserModule implements IModule {

    public static final RegistryObject<Block> PULSER_BLOCK = Registration.defaultBlock("pulser", TileEntityPulser::new);
    public static final RegistryObject<Item> PULSER_ITEM = Registration.fromBlock(PULSER_BLOCK);
    public static final RegistryObject<TileEntityType<TileEntityPulser>> TYPE_PULSER = TILES.register("pulser", () -> TileEntityType.Builder.create(TileEntityPulser::new, PULSER_BLOCK.get()).build(null));

    private static final ResourceLocation CAPABILITY_NAME = new DeepResonanceResourceLocation("crystal_capability/pulser");

    @CapabilityInject(PulserCapability.class)
    public static Capability<PulserCapability> PULSER_CAPABILITY;

    public static PulserBlockConfig pulserBlockConfig;
    public static PulserCrystalConfig pulserCrystalConfig;

    public PulserModule() {
        RegistryHelper.registerEmptyCapability(PulserCapability.class);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        TileEntityResonatingCrystal.registerModifier(PULSER_CAPABILITY);
        MinecraftForge.EVENT_BUS.addGenericListener(TileEntity.class, (Consumer<AttachCapabilitiesEvent<? extends TileEntity>>) tileCaps -> RegistryHelper.registerCapability(tileCaps, CAPABILITY_NAME, PULSER_CAPABILITY, new PulserCapability()));
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {
        Config.configuration.configureSubConfig("pulser", "Pulser settings (power overdrive)", config -> {
            pulserBlockConfig = config.registerConfig(PulserBlockConfig::new, "pulser_block", "Settings for the Pulser (Block)");
            pulserCrystalConfig = config.registerConfig(PulserCrystalConfig::new, "pulser_crystal", "Instability and resistance settings for the crystal");
        });
    }
}
