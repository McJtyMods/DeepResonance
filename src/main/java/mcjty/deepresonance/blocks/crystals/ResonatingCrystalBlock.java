package mcjty.deepresonance.blocks.crystals;

import mcjty.container.GenericBlock;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.material.Material;

public class ResonatingCrystalBlock extends GenericBlock {

    public static int RENDERID_RESONATINGCRYSTAL;

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
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return RENDERID_RESONATINGCRYSTAL;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
