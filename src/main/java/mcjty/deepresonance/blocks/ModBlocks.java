package mcjty.deepresonance.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.container.GenericItemBlock;
import mcjty.deepresonance.blocks.collector.EnergyCollectorSetup;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalBlock;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.ore.ResonatingOreBlock;
import net.minecraftforge.oredict.OreDictionary;

public final class ModBlocks {
    public static ResonatingOreBlock resonatingOreBlock;
    public static ResonatingCrystalBlock resonatingCrystalBlock;

    public static void init() {
        resonatingOreBlock = new ResonatingOreBlock();
        GameRegistry.registerBlock(resonatingOreBlock, "oreResonating");
        OreDictionary.registerOre("oreResonating", resonatingOreBlock);

        resonatingCrystalBlock = new ResonatingCrystalBlock();
        GameRegistry.registerBlock(resonatingCrystalBlock, GenericItemBlock.class, "resonatingCrystalBlock");
        GameRegistry.registerTileEntity(ResonatingCrystalTileEntity.class, "ResonatingCrystalTileEntity");

        GeneratorSetup.setupBlocks();
        EnergyCollectorSetup.setupBlocks();
    }
}
