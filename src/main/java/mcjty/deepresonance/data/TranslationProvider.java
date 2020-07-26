package mcjty.deepresonance.data;

import elec332.core.data.AbstractTranslationProvider;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.setup.FluidRegister;
import mcjty.deepresonance.util.TranslationHelper;
import net.minecraft.data.DataGenerator;

/**
 * Created by Elec332 on 10-1-2020
 */
public class TranslationProvider extends AbstractTranslationProvider {

    TranslationProvider(DataGenerator gen) {
        super(gen, DeepResonance.MODID);
    }

    @Override
    protected void registerTranslations() {
        //Misc
        add("itemGroup.deepresonance", "Deep Resonance");
        add(FluidRegister.liquidCrystal.getAttributes().getTranslationKey(), "Liquid Crystal");

        //Messages
        add(TranslationHelper.getMessageKey("no_tank"), "This is not a tank!");

        //Tooltips
        add(TranslationHelper.getExtendedTooltipKey("tank"),
                "This tank can hold up to 16 buckets of liquid. \n" +
                        "It is also capable of mixing the characteristics \n" +
                        "of liquid crystal. \n" +
                        "Place a comparator next to this tank to detect \n" +
                        "how full the tank is");
        add(TranslationHelper.getTooltipKey(CoreModule.FILTER_MATERIAL_ITEM),
                "This material can be used in the purifier \n" +
                        "to purify the crystal liquid.");
        add(TranslationHelper.getTooltipKey(CoreModule.LIQUID_INJECTOR_ITEM),
                "Creative only item to inject 100mb \n" +
                        "of liquid crystal into a tank.");
        add(TranslationHelper.getTooltipKey("crystal_power"), "You can feel the latent power present in this crystal.");
        add(TranslationHelper.getTooltipKey("crystal_empty"), "This crystal is depleted. Perhaps it still has a future use?");
        add(TranslationHelper.getTooltipKey("crystal_sep"), "Strength/Efficiency/Purity");
        add(TranslationHelper.getTooltipKey("valve"), "This machine will transfer fluids from the upper tank to a tank below if the fluid matches certain conditions.");

        //Items
        addItem(CoreModule.FILTER_MATERIAL_ITEM, "Filter Material");
        addItem(CoreModule.LIQUID_INJECTOR_ITEM, "Liquid Injector");
        addItem(CoreModule.RESONATING_PLATE_ITEM, "Resonating Plate");
        addItem(CoreModule.SPENT_FILTER_ITEM, "Spent Filter Material");
        addItem(CoreModule.MACHINE_FRAME_ITEM, "Resonating Machine Frame");
        addItem(RadiationModule.RADIATION_SUIT_HELMET, "Radiation Suit Helmet");
        addItem(RadiationModule.RADIATION_SUIT_CHESTPLATE, "Radiation Suit Chestplate");
        addItem(RadiationModule.RADIATION_SUIT_LEGGINGS, "Radiation Suit Leggings");
        addItem(RadiationModule.RADIATION_SUIT_BOOTS, "Radiation Suit Boots");

        //Blocks
        addBlock(TankModule.TANK_BLOCK, "Tank");
        addBlock(CoreModule.RESONATING_CRYSTAL_BLOCK, "Resonating Crystal");
        addBlock(CoreModule.RESONATING_ORE_STONE_BLOCK, "Resonating Ore");
        addBlock(CoreModule.RESONATING_ORE_NETHER_BLOCK, "Resonating Nether Ore");
        addBlock(CoreModule.RESONATING_ORE_END_BLOCK, "Resonating End Ore");
        addBlock(RadiationModule.POISONED_DIRT_BLOCK, "Poisoned Dirt");
        addBlock(RadiationModule.DENSE_GLASS_BLOCK, "Dense Glass");
        addBlock(RadiationModule.DENSE_OBSIDIAN_BLOCK, "Dense Obsidian");
        addBlock(CoreModule.RESONATING_PLATE_BLOCK_BLOCK, "Resonating Plate Block");
        addBlock(MachinesModule.VALVE_BLOCK, "Valve");
    }

}
