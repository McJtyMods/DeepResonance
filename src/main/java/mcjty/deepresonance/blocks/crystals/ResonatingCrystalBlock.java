package mcjty.deepresonance.blocks.crystals;

import mcjty.container.GenericBlock;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class ResonatingCrystalBlock extends GenericBlock {

    public ResonatingCrystalBlock() {
        super(Material.glass, ResonatingCrystalTileEntity.class, false);
        setBlockName("resonatingCrystalBlock");
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    public String getSideIconName() {
        return "resonatingOre";
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
