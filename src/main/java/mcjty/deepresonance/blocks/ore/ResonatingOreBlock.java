package mcjty.deepresonance.blocks.ore;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ResonatingOreBlock extends Block {

    public ResonatingOreBlock() {
        super(Material.rock);
        setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setUnlocalizedName(DeepResonance.MODID + ".oreResonating");
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

}
