package mcjty.deepresonance.blocks.cable;

import elec332.core.baseclasses.tileentity.BlockTileBase;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class BlockCable extends BlockTileBase {
    public BlockCable(Class<? extends TileEntity> tileClass, String blockName) {
        super(Material.rock, tileClass, blockName, DeepResonance.MODID);
        setCreativeTab(DeepResonance.tabDeepResonance);
    }
}
