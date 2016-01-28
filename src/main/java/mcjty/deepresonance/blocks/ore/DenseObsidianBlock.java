package mcjty.deepresonance.blocks.ore;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DenseObsidianBlock extends Block {

    public DenseObsidianBlock() {
        super(Material.rock);
        setHardness(50.0f);
        setResistance(2000.0f);
        setStepSound(soundTypePiston);
        setHarvestLevel("pickaxe", 3);
        setUnlocalizedName("dense_obsidian");
        setRegistryName("dense_obsidian");
        setCreativeTab(DeepResonance.tabDeepResonance);
        GameRegistry.registerBlock(this);
    }

}
