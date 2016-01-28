package mcjty.deepresonance.blocks.ore;

import elec332.core.world.WorldHelper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class DenseGlassBlock extends Block {

    public DenseGlassBlock() {
        super(Material.glass);
        setHardness(3.0f);
        setResistance(500.0f);
        setStepSound(soundTypeGlass);
        setHarvestLevel("pickaxe", 2);
        setUnlocalizedName("dense_glass");
        setRegistryName("dense_glass");
        setCreativeTab(DeepResonance.tabDeepResonance);
        GameRegistry.registerBlock(this);
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        Block block = WorldHelper.getBlockAt(world, pos);
        return block != this && super.shouldSideBeRendered(world, pos, side);
    }

}
