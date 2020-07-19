package mcjty.deepresonance.data;

import elec332.core.data.AbstractLootTableProvider;
import elec332.core.data.loottable.AbstractBlockLootTables;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import net.minecraft.data.DataGenerator;

/**
 * Created by Elec332 on 10-1-2020
 */
public class LootTablesProvider extends AbstractLootTableProvider {

    LootTablesProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void registerLootTables() {
        addBlockLootTable(new AbstractBlockLootTables(DeepResonance.MODID) {

            @Override
            protected void registerBlockTables() {
                registerDropSelfLootTable(CoreModule.RESONATING_ORE_STONE_BLOCK);
                registerDropSelfLootTable(CoreModule.RESONATING_ORE_NETHER_BLOCK);
                registerDropSelfLootTable(CoreModule.RESONATING_ORE_END_BLOCK);
                registerDropSelfLootTable(CoreModule.RESONATING_BLOCK_BLOCK);

                registerDropSelfLootTable(RadiationModule.POISONED_DIRT_BLOCK);
                registerSilkTouch(RadiationModule.DENSE_GLASS_BLOCK.get());
                registerDropSelfLootTable(RadiationModule.DENSE_OBSIDIAN_BLOCK);

                //todo: tank & crystal
                registerEmptyLootTable(CoreModule.RESONATING_CRYSTAL_BLOCK.get());
                registerEmptyLootTable(TankModule.TANK_BLOCK.get());
            }

        });
    }

}
