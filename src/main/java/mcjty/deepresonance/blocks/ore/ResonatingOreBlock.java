package mcjty.deepresonance.blocks.ore;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class ResonatingOreBlock extends Block {
    private IIcon icon;

    public ResonatingOreBlock() {
        super(Material.rock);
        setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setBlockName("oreResonating");
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(DeepResonance.MODID + ":resonatingOre");
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
