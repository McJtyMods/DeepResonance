package mcjty.deepresonance.blocks;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.collector.EnergyCollectorSetup;
import mcjty.deepresonance.blocks.crystalizer.CrystalizerSetup;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalBlock;
import mcjty.deepresonance.blocks.debug.DebugBlock;
import mcjty.deepresonance.blocks.gencontroller.GeneratorControllerSetup;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.laser.LaserSetup;
import mcjty.deepresonance.blocks.lens.LensSetup;
import mcjty.deepresonance.blocks.ore.DenseGlassBlock;
import mcjty.deepresonance.blocks.ore.DenseObsidianBlock;
import mcjty.deepresonance.blocks.ore.ResonatingOreBlock;
import mcjty.deepresonance.blocks.ore.ResonatingPlateBlock;
import mcjty.deepresonance.blocks.pedestal.PedestalSetup;
import mcjty.deepresonance.blocks.poisondirt.PoisonedDirtBlock;
import mcjty.deepresonance.blocks.purifier.PurifierSetup;
import mcjty.deepresonance.blocks.smelter.SmelterSetup;
import mcjty.deepresonance.blocks.tank.TankSetup;
import mcjty.deepresonance.blocks.valve.ValveSetup;
import mcjty.deepresonance.items.rftoolsmodule.RFToolsSupport;
import net.minecraft.block.Block;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModBlocks {

    public static ResonatingOreBlock resonatingOreBlock;
    public static ResonatingPlateBlock resonatingPlateBlock;
    public static DenseObsidianBlock denseObsidianBlock;
    public static DenseGlassBlock denseGlassBlock;
    public static ResonatingCrystalBlock resonatingCrystalBlock;
    public static PoisonedDirtBlock poisonedDirtBlock;
    public static Block duct;
    public static MachineFrame machineFrame;
    public static DebugBlock debugBlock;

    public static void init() {
        resonatingOreBlock = new ResonatingOreBlock();

        resonatingPlateBlock = new ResonatingPlateBlock();
        denseObsidianBlock = new DenseObsidianBlock();
        denseGlassBlock = new DenseGlassBlock();
        poisonedDirtBlock = new PoisonedDirtBlock();
        resonatingCrystalBlock = new ResonatingCrystalBlock();
        if (DeepResonance.instance.rftools) {
            RFToolsSupport.initBlocks();
        }

//        duct = new BlockDuct(TileBasicFluidDuct.class, "basicFluidDuct").registerTile().register();

        machineFrame = new MachineFrame();
        debugBlock = new DebugBlock();

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
//        PulserSetup.setupBlocks();
//        SensorSetup.setupBlocks();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        resonatingCrystalBlock.initModel();
        resonatingPlateBlock.initModel();
        denseObsidianBlock.initModel();
        denseGlassBlock.initModel();
        poisonedDirtBlock.initModel();
        resonatingOreBlock.initModel();
        machineFrame.initModel();
        debugBlock.initModel();
        GeneratorSetup.setupModels();
        GeneratorControllerSetup.setupModels();
        EnergyCollectorSetup.setupModels();
        CrystalizerSetup.setupModels();
        SmelterSetup.setupModels();
        TankSetup.setupModels();
        PurifierSetup.setupModels();
        PedestalSetup.setupModels();
        ValveSetup.setupModels();
        LensSetup.setupModels();
        LaserSetup.setupModels();
//        PulserSetup.setupModels();
//        SensorSetup.setupModels();
        if (DeepResonance.instance.rftools) {
            RFToolsSupport.initBlockModels();
        }
    }
}
