package mcjty.deepresonance.modules.machines;

import elec332.core.api.module.ElecModule;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.machines.tile.ValveTileEntity;
import mcjty.deepresonance.modules.machines.util.ValveConfig;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

/**
 * Created by Elec332 on 25-7-2020
 */
@ElecModule(owner = DeepResonance.MODID, name = "Machines")
public class MachinesModule {

    public static final RegistryObject<Block> VALVE_BLOCK = DeepResonance.BLOCKS.register("valve", () -> new BaseBlock(new BlockBuilder().tileEntitySupplier(ValveTileEntity::new).infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("valve")))));

    public static final RegistryObject<Item> VALVE_ITEM = DeepResonance.fromBlock(VALVE_BLOCK);

    public static final ValveConfig valveConfig = new ValveConfig();

    public MachinesModule() {
        DeepResonance.config.configureSubConfig("machines", "Machines module settings", config -> {
            config.registerConfigurableElement(valveConfig, "valve", "Valve settings");
        });
    }

}
