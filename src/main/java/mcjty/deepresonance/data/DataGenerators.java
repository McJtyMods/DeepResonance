package mcjty.deepresonance.data;

import elec332.core.data.AbstractDataGenerator;

/**
 * Created by Elec332 on 10-1-2020
 */
public final class DataGenerators extends AbstractDataGenerator {

    @Override
    public void registerDataProviders(DataRegistry registry) {
        registry.register(RecipesProvider::new);
        registry.register(LootTablesProvider::new);
        registry.register(BlockTagsProvider::new);
        registry.register(ItemTagsProvider::new);
        registry.register(InfusionBonusProvider::new);
        registry.register(TranslationProvider::new);
        registry.register(BlockStateProvider::new);
        registry.register(ItemModelProvider::new);
    }

}
