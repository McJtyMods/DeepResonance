package mcjty.deepresonance.blocks.base;

import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.Container;

/**
 * Created by Elec332 on 20-8-2015.
 */
public class ElecGenericBlockBase<T extends GenericTileEntity, C extends Container> extends GenericDRBlock<T, C> {

    public ElecGenericBlockBase(Material material,
                                Class<? extends T> tileEntityClass,
                                Class<? extends C> containerClass,
                                String blockName) {
        super(material, tileEntityClass, containerClass, blockName, false);
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
    }
}
