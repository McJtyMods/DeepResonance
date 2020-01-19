package mcjty.deepresonance.data;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.setup.FluidRegister;
import mcjty.deepresonance.util.TranslationHelper;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

/**
 * Created by Elec332 on 10-1-2020
 */
public class TranslationProvider extends LanguageProvider {

    TranslationProvider(DataGenerator gen) {
        super(gen, DeepResonance.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
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

        //Items
        addItem(CoreModule.FILTER_MATERIAL_ITEM, "Filter Material");
        addItem(CoreModule.LIQUID_INJECTOR_ITEM, "Liquid Injector");
        addItem(CoreModule.RESONATING_PLATE_ITEM, "Resonating Plate");
        addItem(CoreModule.SPENT_FILTER_ITEM, "Spent Filter Material");

        //Blocks
        addBlock(TankModule.TANK_BLOCK, "Tank");
        addBlock(CoreModule.RESONATING_CRYSTAL_BLOCK, "Resonating Crystal");
        addBlock(CoreModule.MACHINE_FRAME_BLOCK, "Resonating Machine Frame");
    }

}
