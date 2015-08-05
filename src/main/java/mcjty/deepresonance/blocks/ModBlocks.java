package mcjty.deepresonance.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.main.ElecCore;
import mcjty.container.GenericItemBlock;
import mcjty.deepresonance.blocks.cable.BlockCable;
import mcjty.deepresonance.blocks.cable.TileBasicFluidDuct;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalBlock;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.ore.ResonatingOreBlock;
import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;

public final class ModBlocks {

    public static ResonatingOreBlock resonatingOreBlock;
    public static ResonatingCrystalBlock resonatingCrystalBlock;
    public static Block cable;

    public static void init() {
        resonatingOreBlock = new ResonatingOreBlock();
        GameRegistry.registerBlock(resonatingOreBlock, "oreResonating");
        OreDictionary.registerOre("oreResonating", resonatingOreBlock);

        resonatingCrystalBlock = new ResonatingCrystalBlock();
        GameRegistry.registerBlock(resonatingCrystalBlock, GenericItemBlock.class, "resonatingCrystalBlock");
        GameRegistry.registerTileEntity(ResonatingCrystalTileEntity.class, "ResonatingCrystalTileEntity");

        cable = new BlockCable(TileBasicFluidDuct.class, "basicFluidDuct").registerTile().register();

        GeneratorSetup.setupBlocks();
    }
}
