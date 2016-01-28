package mcjty.deepresonance.blocks.ore;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class ResonatingOreBlock extends Block {

    public ResonatingOreBlock() {
        super(Material.rock);
        setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setUnlocalizedName("resonating_ore");
        setRegistryName("resonating_ore");
        setCreativeTab(DeepResonance.tabDeepResonance);
        GameRegistry.registerBlock(this);
        OreDictionary.registerOre("oreResonating", this);
    }

}
