package mcjty.deepresonance.blocks.tank;

import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
import net.minecraft.block.material.Material;

/**
 * Created by Elec332 on 20-8-2015.
 */
public class BlockTank extends ElecGenericBlockBase {

    public BlockTank(String name) {
        super(Material.rock, TileTank.class, name);
    }
}
