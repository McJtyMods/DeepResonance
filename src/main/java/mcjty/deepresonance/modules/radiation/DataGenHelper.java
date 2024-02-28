package mcjty.deepresonance.modules.radiation;

import mcjty.lib.datagen.BaseItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;

import static mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem.RADIATION_PROPERTY;

public class DataGenHelper {

    public static void generateMonitor(BaseItemModelProvider provider) {
        provider.getBuilder(RadiationModule.RADIATION_MONITOR.getId().getPath())
                .parent(provider.getExistingFile(provider.mcLoc("item/handheld")))
                .texture("layer0", "item/monitor/radiationmonitoritem")
                .override().predicate(RADIATION_PROPERTY, 0).model(createMonitorModel(provider, 0)).end()
                .override().predicate(RADIATION_PROPERTY, 1).model(createMonitorModel(provider, 1)).end()
                .override().predicate(RADIATION_PROPERTY, 2).model(createMonitorModel(provider, 2)).end()
                .override().predicate(RADIATION_PROPERTY, 3).model(createMonitorModel(provider, 3)).end()
                .override().predicate(RADIATION_PROPERTY, 4).model(createMonitorModel(provider, 4)).end()
                .override().predicate(RADIATION_PROPERTY, 5).model(createMonitorModel(provider, 5)).end()
                .override().predicate(RADIATION_PROPERTY, 6).model(createMonitorModel(provider, 6)).end()
                .override().predicate(RADIATION_PROPERTY, 7).model(createMonitorModel(provider, 7)).end()
                .override().predicate(RADIATION_PROPERTY, 8).model(createMonitorModel(provider, 8)).end()
                .override().predicate(RADIATION_PROPERTY, 9).model(createMonitorModel(provider, 9)).end()
        ;
    }

    private static ItemModelBuilder createMonitorModel(BaseItemModelProvider provider, int suffix) {
        return provider.getBuilder("radiationmonitoritem" + suffix).parent(provider.getExistingFile(provider.mcLoc("item/handheld")))
                .texture("layer0", "item/monitor/radiationmonitoritem" + suffix);
    }

}
