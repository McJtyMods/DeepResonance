package mcjty.deepresonance.blocks.base;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.container.GenericBlock;
import mcjty.container.GenericItemBlock;
import mcjty.deepresonance.DeepResonance;
import mcjty.entity.GenericTileEntity;
import net.minecraft.block.material.Material;

/**
 * Created by Elec332 on 20-8-2015.
 */
public class ElecGenericBlockBase extends GenericBlock {

    public ElecGenericBlockBase(Material material, Class<? extends GenericTileEntity> tileEntityClass, String blockName) {
        super(DeepResonance.instance, material, tileEntityClass, false);
        this.blockName = blockName;
        setup();
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    /**
     * The code below was derived from ElecCore, you can find the original code here:
     * https://github.com/Elecs-Mods/ElecCore/blob/master/src/main/java/elec332/core/baseclasses/tileentity/BlockTileBase.java
     */

    private void setup(){
        this.setResistance(4.5F);
        this.setHardness(2.0F);
        this.setStepSound(soundTypeStone);
        this.setBlockName(blockName);
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    private final String blockName;

    public ElecGenericBlockBase registerTile() {
        GameRegistry.registerTileEntity(this.tileEntityClass, this.blockName);
        return this;
    }

    public ElecGenericBlockBase register() {
        GameRegistry.registerBlock(this, GenericItemBlock.class, this.blockName);
        return this;
    }

}
