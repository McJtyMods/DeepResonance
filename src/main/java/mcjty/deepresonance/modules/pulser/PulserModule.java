package mcjty.deepresonance.modules.pulser;

import elec332.core.api.module.ElecModule;
import elec332.core.util.RegistryHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.modules.pulser.tile.TileEntityPulser;
import mcjty.deepresonance.modules.pulser.util.PulserCapability;
import mcjty.deepresonance.modules.pulser.util.PulserConfig;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 31-7-2020
 */
@ElecModule(owner = DeepResonance.MODID, name = "Pulser")
public class PulserModule {

    public static final RegistryObject<Block> PULSER_BLOCK = DeepResonance.defaultBlock("pulser", TileEntityPulser::new);
    public static final RegistryObject<Item> PULSER_ITEM = DeepResonance.fromBlock(PULSER_BLOCK);

    private static final ResourceLocation CAPABILITY_NAME = new DeepResonanceResourceLocation("crystal_capability/pulser");

    @CapabilityInject(PulserCapability.class)
    public static Capability<PulserCapability> PULSER_CAPABILITY;

    public static PulserConfig pulserConfig;

    public PulserModule() {
        DeepResonance.config.configureSubConfig("pulser", "Pulser settings (power overdrive)", config -> pulserConfig = config.registerConfig(PulserConfig::new));
        RegistryHelper.registerEmptyCapability(PulserCapability.class);
    }


    @ElecModule.EventHandler
    public void setup(FMLCommonSetupEvent event) {
        TileEntityResonatingCrystal.registerModifier(PULSER_CAPABILITY);
        MinecraftForge.EVENT_BUS.addGenericListener(TileEntity.class, (Consumer<AttachCapabilitiesEvent<? extends TileEntity>>) tileCaps -> RegistryHelper.registerCapability(tileCaps, CAPABILITY_NAME, PULSER_CAPABILITY, new PulserCapability()));
    }

}
