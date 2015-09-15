package mcjty.deepresonance.blocks.ore;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class DenseObsidianBlock extends Block {
    private IIcon icon;

    public DenseObsidianBlock() {
        super(Material.rock);
        setHardness(50.0f);
        setResistance(2000.0f);
        setStepSound(soundTypePiston);
        setHarvestLevel("pickaxe", 3);
        setBlockName("denseObsidian");
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(DeepResonance.MODID + ":denseobsidian");
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return icon;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icon;
    }


}
