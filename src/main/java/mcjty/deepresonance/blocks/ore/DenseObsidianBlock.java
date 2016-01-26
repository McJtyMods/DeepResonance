package mcjty.deepresonance.blocks.ore;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class DenseObsidianBlock extends Block {

    public DenseObsidianBlock() {
        super(Material.rock);
        setHardness(50.0f);
        setResistance(2000.0f);
        setStepSound(soundTypePiston);
        setHarvestLevel("pickaxe", 3);
        setUnlocalizedName(DeepResonance.MODID + ".denseObsidian");
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

}
