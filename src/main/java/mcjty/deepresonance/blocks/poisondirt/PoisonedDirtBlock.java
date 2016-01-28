package mcjty.deepresonance.blocks.poisondirt;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PoisonedDirtBlock extends Block {

    public PoisonedDirtBlock() {
        super(Material.ground);
        setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setUnlocalizedName("poisoned_dirt");
        setRegistryName("poisoned_dirt");
        setCreativeTab(DeepResonance.tabDeepResonance);
        GameRegistry.registerBlock(this);
    }

}
