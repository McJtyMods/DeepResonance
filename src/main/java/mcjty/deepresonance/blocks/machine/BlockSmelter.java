package mcjty.deepresonance.blocks.machine;

import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
import net.minecraft.block.material.Material;

/**
 * Created by Elec332 on 20-8-2015.
 */
public class BlockSmelter extends ElecGenericBlockBase {

    public BlockSmelter(String blockName) {
        super(Material.rock, TileSmelter.class, blockName);
    }

}
