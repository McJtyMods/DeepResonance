package mcjty.deepresonance.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * Created by Elec332 on 10-1-2020
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new RecipesProvider(generator));
            generator.addProvider(new LootTablesProvider(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(new TranslationProvider(generator));
            generator.addProvider(new BlockStateProvider(generator, event.getExistingFileHelper()));
            generator.addProvider(new ItemModelProvider(generator, event.getExistingFileHelper()));
        }
    }

}
