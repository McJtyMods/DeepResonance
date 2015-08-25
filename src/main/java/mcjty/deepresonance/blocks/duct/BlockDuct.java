package mcjty.deepresonance.blocks.duct;

import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
import mcjty.deepresonance.client.render.ModRenderers;
import mcjty.entity.GenericTileEntity;
import net.minecraft.block.material.Material;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class BlockDuct extends ElecGenericBlockBase {

    public BlockDuct(Class<? extends GenericTileEntity> tileClass, String blockName) {
        super(Material.rock, tileClass, blockName);
    }

    @Override
    public int getRenderType() {
        return ModRenderers.ductRenderID;
    }
}
