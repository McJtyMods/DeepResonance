package mcjty.deepresonance.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.deepresonance.blocks.collector.EnergyCollectorSetup;
import mcjty.deepresonance.blocks.crystalizer.CrystalizerSetup;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalBlock;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.blocks.gencontroller.GeneratorControllerSetup;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.laser.LaserSetup;
import mcjty.deepresonance.blocks.lens.LensSetup;
import mcjty.deepresonance.blocks.ore.DenseGlassBlock;
import mcjty.deepresonance.blocks.ore.DenseObsidianBlock;
import mcjty.deepresonance.blocks.ore.ResonatingOreBlock;
import mcjty.deepresonance.blocks.pedestal.PedestalSetup;
import mcjty.deepresonance.blocks.poisondirt.PoisonedDirtBlock;
import mcjty.deepresonance.blocks.purifier.PurifierSetup;
import mcjty.deepresonance.blocks.smelter.SmelterSetup;
import mcjty.deepresonance.blocks.tank.TankSetup;
import mcjty.deepresonance.blocks.valve.ValveSetup;
import mcjty.lib.container.GenericItemBlock;
import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;

public final class ModBlocks {

    public static ResonatingOreBlock resonatingOreBlock;
    public static DenseObsidianBlock denseObsidianBlock;
    public static DenseGlassBlock denseGlassBlock;
    public static ResonatingCrystalBlock resonatingCrystalBlock;
    public static PoisonedDirtBlock poisonedDirtBlock;
    public static Block duct;
    public static MachineFrame machineFrame;

    public static void init() {
        resonatingOreBlock = new ResonatingOreBlock();
        GameRegistry.registerBlock(resonatingOreBlock, "oreResonating");
        OreDictionary.registerOre("oreResonating", resonatingOreBlock);

        denseObsidianBlock = new DenseObsidianBlock();
        GameRegistry.registerBlock(denseObsidianBlock, "denseObsidian");

        denseGlassBlock = new DenseGlassBlock();
        GameRegistry.registerBlock(denseGlassBlock, "denseGlass");

        poisonedDirtBlock = new PoisonedDirtBlock();
        GameRegistry.registerBlock(poisonedDirtBlock, "poisonedDirt");

        resonatingCrystalBlock = new ResonatingCrystalBlock();
        GameRegistry.registerBlock(resonatingCrystalBlock, GenericItemBlock.class, "resonatingCrystalBlock");
        GameRegistry.registerTileEntity(ResonatingCrystalTileEntity.class, "ResonatingCrystalTileEntity");

//        duct = new BlockDuct(TileBasicFluidDuct.class, "basicFluidDuct").registerTile().register();

        machineFrame = new MachineFrame();
        GameRegistry.registerBlock(machineFrame, "machineFrame");

        GeneratorSetup.setupBlocks();
        GeneratorControllerSetup.setupBlocks();
        EnergyCollectorSetup.setupBlocks();
        CrystalizerSetup.setupBlocks();
        SmelterSetup.setupBlocks();
        TankSetup.setupBlocks();
        PurifierSetup.setupBlocks();
        PedestalSetup.setupBlocks();
        ValveSetup.setupBlocks();
        LensSetup.setupBlocks();
        LaserSetup.setupBlocks();
    }
}
