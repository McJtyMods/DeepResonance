package mcjty.deepresonance.data;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = DeepResonance.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new Recipes(generator));
            generator.addProvider(new LootTables(generator));
            generator.addProvider(new ItemTags(generator, event.getExistingFileHelper()));
            generator.addProvider(new BlockTags(generator, event.getExistingFileHelper()));
        }
        if (event.includeClient()) {
            generator.addProvider(new BlockStates(generator, event.getExistingFileHelper()));
            generator.addProvider(new Items(generator, event.getExistingFileHelper()));
        }
    }

    // @todo 1.16
//        registry.register(BlockTagsProvider::new);
//        registry.register(ItemTagsProvider::new);
//        registry.register(InfusionBonusProvider::new);

}
