package mcjty.deepresonance.modules.machines;

import elec332.core.api.module.ElecModule;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.machines.tile.PulserTileEntity;
import mcjty.deepresonance.modules.machines.tile.PurifierTileEntity;
import mcjty.deepresonance.modules.machines.tile.SmelterTileEntity;
import mcjty.deepresonance.modules.machines.tile.ValveTileEntity;
import mcjty.deepresonance.modules.machines.util.*;
import mcjty.deepresonance.util.DeepResonanceBlock;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 25-7-2020
 */
@ElecModule(owner = DeepResonance.MODID, name = "Machines")
public class MachinesModule {

    public static final RegistryObject<Block> VALVE_BLOCK = DeepResonance.BLOCKS.register("valve", () -> new DeepResonanceBlock(new BlockBuilder().tileEntitySupplier(ValveTileEntity::new).infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("valve")))) {

        @Override
        protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        }

    });
    public static final RegistryObject<Block> SMELTER_BLOCK = DeepResonance.BLOCKS.register("smelter", () -> new DeepResonanceBlock(new BlockBuilder().tileEntitySupplier(SmelterTileEntity::new).infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("smelter")))) {

        @Override
        public void addProperties(Consumer<IProperty<?>> stateContainer) {
            stateContainer.accept(BlockStateProperties.LIT);
        }

    });
    public static final RegistryObject<Block> PURIFIER_BLOCK = DeepResonance.BLOCKS.register("purifier", () -> new DeepResonanceBlock(new BlockBuilder().tileEntitySupplier(PurifierTileEntity::new).infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("purifier")))));
    public static final RegistryObject<Block> PULSER_BLOCK = DeepResonance.BLOCKS.register("pulser", () -> new DeepResonanceBlock(new BlockBuilder().tileEntitySupplier(PulserTileEntity::new).infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("pulser")))));

    public static final RegistryObject<Item> VALVE_ITEM = DeepResonance.fromBlock(VALVE_BLOCK);
    public static final RegistryObject<Item> SMELTER_ITEM = DeepResonance.fromBlock(SMELTER_BLOCK);
    public static final RegistryObject<Item> PURIFIER_ITEM = DeepResonance.fromBlock(PURIFIER_BLOCK);
    public static final RegistryObject<Item> PULSER_ITEM = DeepResonance.fromBlock(PULSER_BLOCK);

    public static CollectorConfig collectorConfig;
    public static CrystallizerConfig crystallizerConfig;
    public static LaserConfig laserConfig;
    public static PulserConfig pulserConfig;
    public static PurifierConfig purifierConfig;
    public static SmelterConfig smelterConfig;
    public static ValveConfig valveConfig;

    public MachinesModule() {
        DeepResonance.config.configureSubConfig("machines", "Machines module settings", config -> {
            collectorConfig = config.registerConfig(CollectorConfig::new, "collector", "Collector settings");
            crystallizerConfig = config.registerConfig(CrystallizerConfig::new, "crystallizer", "Crystallizer settings");
            laserConfig = config.registerConfig(LaserConfig::new, "laser", "Laser settings");
            pulserConfig = config.registerConfig(PulserConfig::new, "pulser", "Pulser settings");
            purifierConfig = config.registerConfig(PurifierConfig::new, "purifier", "Purifier settings");
            smelterConfig = config.registerConfig(SmelterConfig::new, "smelter", "Smelter settings");
            valveConfig = config.registerConfig(ValveConfig::new, "valve", "Valve settings");
        });
    }

}
