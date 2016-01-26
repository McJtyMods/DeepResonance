package mcjty.deepresonance.blocks.ore;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class ResonatingPlateBlock extends Block {

    public ResonatingPlateBlock() {
        super(Material.rock);
        setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setUnlocalizedName(DeepResonance.MODID + ".blockResonating");
        if (ConfigMachines.PlateBlock.radiationStrength > 0) {
            setTickRandomly(true);
        }
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (ConfigMachines.PlateBlock.radiationStrength <= 0) {
            return;
        }
        int powered = world.getStrongPower(pos);
        if (powered > 0) {
            DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
            GlobalCoordinate thisCoordinate = new GlobalCoordinate(pos, WorldHelper.getDimID(world));
            if (radiationManager.getRadiationSource(thisCoordinate) == null) {
                Logging.log("Created radiation source with radius " + ConfigMachines.PlateBlock.radiationRadius + " and strength " + ConfigMachines.PlateBlock.radiationStrength);
            }
            DRRadiationManager.RadiationSource radiationSource = radiationManager.getOrCreateRadiationSource(thisCoordinate);
            radiationSource.update(ConfigMachines.PlateBlock.radiationRadius, ConfigMachines.PlateBlock.radiationStrength, ConfigMachines.PlateBlock.radiationTicks);
            radiationManager.save(world);
        }
    }

}
