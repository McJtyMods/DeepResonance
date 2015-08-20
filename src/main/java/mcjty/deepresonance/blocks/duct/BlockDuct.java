package mcjty.deepresonance.blocks.duct;

import elec332.core.baseclasses.tileentity.BlockTileBase;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
import mcjty.deepresonance.client.render.ModRenderers;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class BlockDuct extends ElecGenericBlockBase {

    public BlockDuct(Class<? extends TileEntity> tileClass, String blockName) {
        super(Material.rock, tileClass, blockName);
    }

    @Override
    public int getRenderType() {
        return ModRenderers.ductRenderID;
    }
}
