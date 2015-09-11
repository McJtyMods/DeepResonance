package mcjty.deepresonance.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.container.GenericItemBlock;
import mcjty.deepresonance.blocks.collector.EnergyCollectorSetup;
import mcjty.deepresonance.blocks.crystalizer.CrystalizerBlock;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalBlock;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.blocks.duct.BlockDuct;
import mcjty.deepresonance.blocks.duct.TileBasicFluidDuct;
import mcjty.deepresonance.blocks.gencontroller.GeneratorControllerSetup;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.smelter.SmelterBlock;
import mcjty.deepresonance.blocks.ore.ResonatingOreBlock;
import mcjty.deepresonance.blocks.poisondirt.PoisonedDirtBlock;
import mcjty.deepresonance.blocks.tank.BlockTank;
import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;

public final class ModBlocks {

    public static ResonatingOreBlock resonatingOreBlock;
    public static ResonatingCrystalBlock resonatingCrystalBlock;
    public static PoisonedDirtBlock poisonedDirtBlock;
    public static Block duct, smelter;
    public static BlockTank tank;
    public static CrystalizerBlock crystalizer;
    public static MachineFrame machineFrame;

    public static void init() {
        resonatingOreBlock = new ResonatingOreBlock();
        GameRegistry.registerBlock(resonatingOreBlock, "oreResonating");
        OreDictionary.registerOre("oreResonating", resonatingOreBlock);

        poisonedDirtBlock = new PoisonedDirtBlock();
        GameRegistry.registerBlock(poisonedDirtBlock, "poisonedDirt");

        resonatingCrystalBlock = new ResonatingCrystalBlock();
        GameRegistry.registerBlock(resonatingCrystalBlock, GenericItemBlock.class, "resonatingCrystalBlock");
        GameRegistry.registerTileEntity(ResonatingCrystalTileEntity.class, "ResonatingCrystalTileEntity");

        duct = new BlockDuct(TileBasicFluidDuct.class, "basicFluidDuct").registerTile().register();
        smelter = new SmelterBlock("smelter").registerTile().register();

        tank = new BlockTank("tank");
        tank.registerTile().register();

        crystalizer = new CrystalizerBlock("crystalizer");
        crystalizer.registerTile().register();

        machineFrame = new MachineFrame();
        GameRegistry.registerBlock(machineFrame, "machineFrame");

        GeneratorSetup.setupBlocks();
        GeneratorControllerSetup.setupBlocks();
        EnergyCollectorSetup.setupBlocks();
    }
}
