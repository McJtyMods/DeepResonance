package mcjty.deepresonance.blocks.ore;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.lib.varia.Coordinate;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class ResonatingPlateBlock extends Block {
    private IIcon icon;

    public ResonatingPlateBlock() {
        super(Material.rock);
        setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setBlockName("blockResonating");
        if (ConfigMachines.PlateBlock.radiationStrength > 0) {
            setTickRandomly(true);
        }
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(DeepResonance.MODID + ":resonatingPlateBlock");
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return icon;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icon;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (ConfigMachines.PlateBlock.radiationStrength <= 0) {
            return;
        }
        int powered = world.getBlockPowerInput(x, y, z);
        if (powered > 0) {
            DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
            GlobalCoordinate thisCoordinate = new GlobalCoordinate(new Coordinate(x, y, z), world.provider.dimensionId);
            if (radiationManager.getRadiationSource(thisCoordinate) == null) {
                Logging.log("Created radiation source with radius " + ConfigMachines.PlateBlock.radiationRadius + " and strength " + ConfigMachines.PlateBlock.radiationStrength);
            }
            DRRadiationManager.RadiationSource radiationSource = radiationManager.getOrCreateRadiationSource(thisCoordinate);
            radiationSource.update(ConfigMachines.PlateBlock.radiationRadius, ConfigMachines.PlateBlock.radiationStrength, ConfigMachines.PlateBlock.radiationTicks);
            radiationManager.save(world);
        }
    }
}
