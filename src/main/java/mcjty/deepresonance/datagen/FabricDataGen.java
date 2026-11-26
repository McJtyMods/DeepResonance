package mcjty.deepresonance.datagen;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.radiation.DataGenHelper;
import mcjty.lib.datagen.DataGen;
import mcjty.rftoolsbase.tools.ClientItemDefinitions;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import java.util.Map;

public class FabricDataGen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        var pack = generator.createPack();
        pack.addProvider((output, lookup) -> {
            var data = new DataGen(output, lookup);
            DeepResonance.modules.datagen(data, lookup);
            data.generate();
            return data;
        });
        pack.addProvider((output, lookup) -> new ClientItemDefinitions(output, DeepResonance.MODID,
                Map.of("radiation_monitor", DataGenHelper.monitorDefinition())));
    }
}
